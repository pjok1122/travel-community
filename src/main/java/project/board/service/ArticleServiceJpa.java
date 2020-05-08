package project.board.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.domain.dto.ArticleDto2;
import project.board.domain.dto.Page;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.entity.UploadFile;
import project.board.entity.dto.ArticleDetail;
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
import project.board.repository.UploadFileRepositoryJpa;
import project.board.util.UploadFileUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceJpa {

	private final ArticleRepositoryJpa articleRepository;
	private final MemberRepositoryJpa memberRepository;
	private final BookmarkRepositoryJpa bookmarkRepository;
	private final ArticleLikeRepositoryJpa articleLikeRepository;
	private final PostFileRepositoryJpa postFileRepository;
	private final UploadFileRepositoryJpa uploadFileRepository;
	private final UploadFileUtils uploadFileUtils;
	
	@Value("${image.baseUrl}")
	private String imageBaseUrl;
	
	@Value("${image.dir.path.format}")
	private String dirPathFormat;
	
	/**
	 * Category, Nation, search를 만족하는 게시물을 조회한다.
	 * pageAndSort에는 요구하는 페이지와 정렬조건이 담겨있다.
	 * 
	 * @param category
	 * @param nation
	 * @param pageAndSort
	 * @param search
	 * @return Page(조건을 만족하는 게시물 목록)
	 */
	public Page<ArticleDto2> find(Category category, Nation nation, PageAndSort ps, String search) {
		Page<ArticleDto2> page = new Page<ArticleDto2>(ps.getPage());
		page.setNumberOfRecordsAndMakePageInfo(
				articleRepository.count(category.toString(), nation.toString(), search));
		page.setList(articleRepository.findAll(category.toString(), nation.toString(), search,
				ps.getSort().toString(), page.getOffset(), page.getRecordsPerPage()));
		return page;
	}
	
	/**
	 * 게시물을 저장한다.
	 * @param memberId
	 * @param article
	 * @return articleId
	 */
	@Transactional
	public Long save(Long memberId, ArticleForm form) {
		//엔티티 조회
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException("Doesn't exist "+ memberId));
		
		//엔티티 생성
		Article article = new Article(member, form.getCategory(), form.getTitle(), form.getContent(), form.getNation());
		
		//PostFiles 추가
		article.addPostFiles(strToPostFiles(form.getImages()));
		
		//엔티티 저장
		articleRepository.save(article);
		
		return article.getId();
	}

	
	/**
	 * ArticleForm의 imageNames를 List<PostFile>로 변환해 제공한다.
	 * @param imageNames
	 * @return List<PostFile>
	 */
	private List<PostFile> strToPostFiles(String imageNames) {
		if(imageNames == null) return new ArrayList<PostFile>();
		
		List<String> fileNames = Arrays.asList(imageNames.trim().split(" "));
		
		// 이미지 이름을 List에 저장한다.
		fileNames = fileNames.stream().filter(name-> name.length() > imageBaseUrl.length()+dirPathFormat.length())
						.map(name -> name.substring(imageBaseUrl.length()+dirPathFormat.length()))
						.collect(Collectors.toList());
		
		if(fileNames.isEmpty()) return new ArrayList<PostFile>();
		
		//임시저장소에서 불러온다.
		List<UploadFile> uploadFiles = uploadFileRepository.findByFileNames(fileNames);
		
		return uploadFiles.stream().map(uf-> new PostFile(uf))
			.collect(Collectors.toList());

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
		
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException());
		
		//게시물 상태 조회
		int liked = articleLikeRepository.countByMemberAndArticle(member, article);
		int bookmarked = bookmarkRepository.countByMemberAndArticle(member, article);
		
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
		System.out.println(oldArticle.getPostFiles().size());
		oldArticle.getPostFiles().clear();
		System.out.println(oldArticle.getPostFiles().size());
		oldArticle.update(article.getTitle(), article.getContent(), article.getCategory(), article.getNation(), strToPostFiles(article.getImages()));
		System.out.println(oldArticle.getPostFiles().size());
	}
	
	/**
	 * images에 해당하는 PostFile을 조회한 후, 임시저장소에서 게시물 저장소로 파일을 복사한다.
	 * @param images
	 */
	public void uploadToPost(String images) {
		uploadFileUtils.tempFileCopyAsPostFile(strToPostFiles(images));
	}
	

	/**
	 * 게시물의 소유주가 맞다면 게시물을 삭제한다. 
	 * @param memberId
	 * @param articleId
	 */
	public void remove(Long memberId, Long articleId) {
		Article article = getMyArticle(memberId, articleId);
		articleRepository.delete(article);
	}

	
	private Boolean isArticleOwner(Long memberId, Article article) {
		return article.getMember().getId().equals(memberId) ? true : false;
	}

}
