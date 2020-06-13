package project.board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.domain.dto.MyPage;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.entity.dto.ArticleDetail;
import project.board.entity.dto.ArticleDto2;
import project.board.entity.dto.ArticleForm;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.exception.EntityOwnerMismatchException;
import project.board.exception.NoExistException;
import project.board.repository.ArticleLikeRepositoryJpa;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.BookmarkRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.PostFileRepositoryJpa;
import project.board.util.UploadFileUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceJpa {

	private final ArticleRepositoryJpa articleRepository;
	private final MemberRepositoryJpa memberRepository;
	private final BookmarkRepositoryJpa bookmarkRepository;
	private final ArticleLikeRepositoryJpa articleLikeRepository;
	private final ImageServiceJpa imageService;
	private final PostFileRepositoryJpa postFileRepository;
	private final UploadFileUtils uploadFileUtils;
	private final TempArticleServiceJpa tempArticleService;
	
	private final int MAIN_ARTICLE_NUM = 5;
	
	@Value("${image.baseUrl}")
	private String imageBaseUrl;
	
	@Value("${image.dir.path.format}")
	private String dirPathFormat;
	
	/**
	 * Category, Nation, search, page를 만족하는 게시물을 인기순으로 조회한다.
	 * @param category
	 * @param nation
	 * @param pageNo
	 * @param search
	 * @return MyPage<ArticleDto2>
	 */
	@Cacheable(cacheNames = "popular", sync = true)
	public MyPage<ArticleDto2> findPopular(Category category, Nation nation, int pageNo, String search) {
		MyPage<ArticleDto2> page = new MyPage<ArticleDto2>(pageNo);
		page.setNumberOfRecordsAndMakePageInfo(
				articleRepository.count(category.toString(), nation.toString(), search));
		page.setList(articleRepository.findPopularAll(category.toString(), nation.toString(), search, page.getOffset(), page.getRecordsPerPage()));
		return page;
	}
	
	/**
	 * Category, Nation, search, page를 만족하는 게시물을 최신순으로 조회한다.
	 * @param category
	 * @param nation
	 * @param pageNo
	 * @param search
	 * @return MyPage<ArticleDto2>
	 */
	public MyPage<ArticleDto2> findNew(Category category, Nation nation, int pageNo, String search) {
		MyPage<ArticleDto2> page = new MyPage<ArticleDto2>(pageNo);
		page.setNumberOfRecordsAndMakePageInfo(
				articleRepository.count(category.toString(), nation.toString(), search));
		page.setList(articleRepository.findNewAll(category.toString(), nation.toString(), search, page.getOffset(), page.getRecordsPerPage()));
		return page;
	}
	
	/**
	 * 게시물을 저장한다.
	 * @param memberId
	 * @param articleForm
	 * @return articleId
	 */
	@Transactional
	public Long save(Long memberId, ArticleForm form) {
		//엔티티 조회
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException("Doesn't exist "+ memberId));
		
		//엔티티 생성
		Article article = new Article(member, form.getCategory(), form.getTitle(), form.getContent(), form.getNation());
		
		//PostFiles 추가
		article.addPostFiles(imageService.strToPostFiles(form.getImages()));
		
		//엔티티 저장
		articleRepository.save(article);
		
		return article.getId();
	}
	
	/**
	 * TempArticle을 Article로 변경한다.
	 * @param memberId
	 * @param article
	 * @return
	 */
	@Transactional
	public Long tempToPost(Long memberId, @Valid ArticleForm article) {
		//게시물 저장
		Long articleId = save(memberId, article);

		//임시저장 글 삭제
		tempArticleService.remove(memberId, article.getArticleId());
		
		return articleId;
	}

	/**
	 * memberId가 articleId에 해당하는 글의 작성자인지 확인하고, 작성자라면 게시물을 조회한다.
	 * @param memberId
	 * @param articleId
	 * @return article
	 */
	public Article getMyArticle(Long memberId, Long articleId) {
		//게시물이 없는 경우 예외 발생
		Article article = articleRepository.findDetailById(articleId).orElseThrow(()-> new NoExistException("doesn't exist this article. id=" + articleId));
		//주인이 아닌 경우 예외 발생
		if(!isArticleOwner(memberId, article)) throw new EntityOwnerMismatchException();
		
		return article;
	}

	/**
	 * articleId에 해당하는 정보를 조회한다.
	 * 
	 * @param memberId (로그인 유저)
	 * @param articleId
	 * @return articleDetail : 게시물 정보, 사진정보, 유저의 게시물 좋아요,북마크 정보를 반환한다.
	 */	
	@Transactional
	public ArticleDetail getDetail(Long memberId, Long articleId) {
		//(article -> member,  article -> postFile) Fetch Join
		Article article = articleRepository.findDetailById(articleId).orElseThrow(()->new NoExistException());
		
		Member member = null;
		if(memberId != null) member = memberRepository.findById(memberId).orElseGet(null);
		
		//게시물 상태 조회
		boolean liked = articleLikeRepository.existsByMemberAndArticle(member, article);
		boolean bookmarked = bookmarkRepository.existsByMemberAndArticle(member, article);
		
		//조회수 증가
		article.hitUp();
		
		return new ArticleDetail(article, liked, bookmarked);
	}
	/**
	 * 게시물을 수정한다. 게시물의 주인이 아닐 경우 예외가 발생한다.
	 * @param memberId
	 * @param articleId
	 * @param article
	 */
	@Transactional
	public void update(Long memberId, @Valid ArticleForm article) {
		Article oldArticle = getMyArticle(memberId, article.getArticleId());
		oldArticle.getPostFiles().clear();
		oldArticle.update(article.getTitle(), article.getContent(), article.getCategory(), article.getNation(), imageService.strToPostFiles(article.getImages()));
	}
	
	/**
	 * 회원이 작성한 게시물 상위 10개를 조회한다.
	 * @param memberId 회원 번호
	 * @param pageNo 페이지 번호
	 * @return Page<Article>
	 */
	public Page<Article> getMyArticles(Long memberId, int pageNo) {
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException());
		PageRequest pageable = PageRequest.of(pageNo, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
		return articleRepository.findByMember(member, pageable);
	}
	
	/**
	 * images에 해당하는 PostFile을 조회한 후, 임시저장소에서 게시물 저장소로 파일을 복사한다.
	 * @param images
	 */
	public void uploadToPost(String images) {
		uploadFileUtils.tempFileCopyAsPostFile(imageService.strToPostFiles(images));
	}
	

	/**
	 * 게시물의 소유주가 맞다면 게시물을 삭제한다. 
	 * @param memberId
	 * @param articleId
	 */
	@Transactional
	public void remove(Long memberId, Long articleId) {
		Article article = getMyArticle(memberId, articleId);
		articleRepository.delete(article);
	}
	
	
	public Map<String, Object> getMainArticle() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArticleDto2> newest = articleRepository.findNewAll("ALL", "ALL", null, 0, MAIN_ARTICLE_NUM);
		List<ArticleDto2> populars = articleRepository.findPopularAll("ALL", "ALL", null, 0, MAIN_ARTICLE_NUM);
		
		//이미지 가져오기
		List<PostFile> newImages = getImages(newest.stream().map(a -> a.getId()).collect(Collectors.toList()));
		List<PostFile> popularImages = getImages(populars.stream().map(a -> a.getId()).collect(Collectors.toList()));

		//이미지 선택하기
		if(newImages.size()>4)	newImages = newImages.subList(0, 4);
		
		popularImages = chooseImages(newImages, popularImages);
		map.put("newest", newest);
		map.put("popular", populars);
		map.put("newImages", newImages);
		map.put("popularImages", popularImages);

		return map;
	}
	
	private Boolean isArticleOwner(Long memberId, Article article) {
		return article.getMember().getId().equals(memberId) ? true : false;
	}
	
	private List<PostFile> chooseImages(List<PostFile> newImages, List<PostFile> popularImages) {
		ArrayList<PostFile> temp = new ArrayList<PostFile>();
		int count = 0;
		for(PostFile file : popularImages) {
			if(count ==4) {
				break;
			}
			if(!newImages.contains(file)) {
				temp.add(file);
				count++;
			}
		}
		return temp;
	}

	private List<PostFile> getImages(List<Long> articleIds){
		if(articleIds.isEmpty()) {
			return new ArrayList<PostFile>();
		}
		return postFileRepository.selectByArticleIds(articleIds);
	}






}
