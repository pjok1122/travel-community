package project.board.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import project.board.domain.Article;
import project.board.domain.Bookmark;
import project.board.domain.UploadFile;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Member;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;
import project.board.repository.ArticleLikeRepository;
import project.board.repository.ArticleRepository;
import project.board.repository.BookmarkRepository;
import project.board.repository.CategoryRepository;
import project.board.repository.MemberRepository;
import project.board.repository.PostFileRepository;
import project.board.repository.UploadFileRepository;
import project.board.util.ScriptEscapeUtils;
import project.board.util.UploadFileUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final ArticleRepositoryJpa articleRepositoryJpa;
	private final ArticleLikeRepository articleLikeRepository;
	private final CategoryRepository categoryRepository;
	private final BookmarkRepository bookmarkRepository;
	private final UploadFileRepository uploadFileRepository;
	private final PostFileRepository postFileRepository;
	private final ScriptEscapeUtils scriptEscaper;
	private final MemberRepository memberRepository;
	private final MemberRepositoryJpa memberRepositoryJpa;

	private final int TEMP_ARTICLE_MAX_SIZE = 10;
	private final int MAIN_ARTICLE_NUM = 5;
	private final String NEWEST = "NEWEST";
	private final String POPULAR = "POPULAR";

	@Value("${image.baseUrl}")
	private String imageBaseUrl;

	@Value("${image.postUrl}")
	private String imagePostUrl;

	@Value("${image.save.path}")
	private String imageSavePath;

	@Value("${image.post.path}")
	private String imagePostPath;
	
	@Value("${image.dir.path.format}")
	private String dirPathFormat;

	// TODO : delete package path
	public org.springframework.data.domain.Page<project.board.entity.Article> getArticleByMemberId(Long memberId, String status, Page paging) {
		Member member = memberRepositoryJpa.findById(memberId)
										   .orElseThrow(() -> new IllegalArgumentException());
		Pageable pageable = PageRequest.of(paging.getCurrentPageNo() - 1, paging.getRecordsPerPage());
		return articleRepositoryJpa.findByMemberAndStatus(member, status, pageable);
	}

	public List<ArticleDto> getTempArticleByMemberId(Long id, Page paging) {
		return articleRepository.selectArticleListByMemberId(id, paging.getOffset(), paging.getRecordsPerPage(),
				"TEMP");
	}

	public Long countArticle(Long memberId, String status) {
		//TODO : 예외 핸들링 필요.
		Member member = memberRepositoryJpa.findById(memberId)
										   .orElseThrow(() -> new IllegalArgumentException());
		return articleRepositoryJpa.countAllByMemberAndStatus(member, status);
	}

	public Map<String, Object> getArticleList(Category category, Nation nation, PageAndSort ps, String search) {
		Map<String, Object> map = new HashMap<String, Object>();
		Page paging = new Page(ps.getPage());
		paging.setNumberOfRecordsAndMakePageInfo(
				articleRepository.getArticleCnt(category.getKrValue(), nation.getKrValue(), search));
		paging.setList(articleRepository.selectArticleList(category.getKrValue(), nation.getKrValue(), search,
				ps.getSort().toString(), paging.getOffset(), paging.getRecordsPerPage()));
		map.put("page", paging);
		return map;
	}

	@Transactional
	public Long createArticle(Article article, String imageNames, Long memberId) {
		Long categoryId = categoryRepository.selectIdByTitle(article.getCategory().getKrValue());
		article.setMemberId(memberId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		article.setCategoryId(categoryId);

		article.setContent(article.getContent().replaceAll("src=\""+imageBaseUrl, "src=\""+imagePostUrl));
		if (article.getId() != null) {
			articleRepository.updateTempToPermanent(article);
		} else {
			articleRepository.insertArticle(article);
		}

		insertPostImages(imageNames, article.getId());

		return article.getId();
	}

	@Transactional
	private void insertPostImages(String imageNames, Long articleId) {
		List<String> fileNames = Arrays.asList(imageNames.trim().split(" "));
		
		// 이미지 이름을 List에 저장한다.
		fileNames = fileNames.stream().filter(name-> name.length() > imageBaseUrl.length()+dirPathFormat.length())
						.map(name -> name.substring(imageBaseUrl.length()+dirPathFormat.length()))
						.collect(Collectors.toList());
		
		if(fileNames.isEmpty()) {
			return;
		}
		
		// upload_file에서 조회하고 bulk_update한다.
		List<UploadFile> postFiles = uploadFileRepository.selectByFileNames(fileNames);
		postFiles.forEach(pf ->	pf.setArticleId(articleId));
		
		if(postFiles.isEmpty()) {
			return;
		}

		// upload_file을 기반으로 post_file를 insert한다.
		postFileRepository.insertPostFiles(postFiles);

		// 이미지 파일을 복사해서 새로운 폴더에서 관리한다.
		UploadFileUtils.tempFileCopyAsPostFile(imageSavePath, imagePostPath, postFiles);

	}

	public Boolean checkTempArticleWritable(Long memberId) {
		if (countArticle(memberId, "TEMP") < TEMP_ARTICLE_MAX_SIZE) {
			return true;
		} else {
			return false;
		}
	}

	@Transactional
	public Long createTempArticle(Article article, Long memberId) {
		article.setMemberId(memberId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		articleRepository.insertTempArticle(article);
		return article.getId();
	}
	
	@Transactional
	public Map<String, Object> getArticleDetailById(Long memberId, Long articleId) {
		ArticleDto article = articleRepository.selectArticleById(articleId);
		
		//임시저장글인지 검증
		if(checkStatusTemp(article)) return null;
		
		//GPS 정보 추가
		article.setGpsInfo(postFileRepository.selectByArticleId(articleId));
		
		//조회수 Up
		increaseHitById(articleId);
		
		//유저가 해당 글에 좋아요와 북마크를 했는지에 대한 상태를 조회한다.
		Map<String, Object> articleMap = checkArticleSatus(memberId, articleId);
		articleMap.put("article", article);
		
		return articleMap;
	}
	
	public ArticleDto getUpdateForm(Long memberId, Long articleId) {
		ArticleDto article = articleRepository.selectArticleById(articleId);
		if(!checkArticleOwner(memberId, article)) return null;
		return article;
	}
	

	@Transactional
	public void modifyArticle(Long articleId, @Valid Article article, String images) {
		
		insertPostImages(images, articleId);
		
		Long categoryId = categoryRepository.selectIdByTitle(article.getCategory().getKrValue());
		article.setId(articleId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		article.setCategoryId(categoryId);
		article.setContent(article.getContent().replaceAll("src=\""+imageBaseUrl, "src=\""+imagePostUrl));
		articleRepository.updateArticle(article);
	}
	
	@Transactional
	public void removeArticleById(Long articleId) {
		articleRepository.deleteArticleById(articleId);
	}

	@Transactional
	public void increaseHitById(Long articleId) {
		articleRepository.updateHitById(articleId);
	}

	public int checkLikeStatus(Long memberId, Long articleId) {
		Long id = articleLikeRepository.selectByMemberIdAndArticleId(memberId, articleId);
		if (id == null)
			return 0;
		else
			return 1;
	}

	@Transactional
	public int modifyLikeStatus(Long memberId, Long articleId) {
		Long id = articleLikeRepository.selectByMemberIdAndArticleId(memberId, articleId);

		if (id == null) {
			articleLikeRepository.insertByMemberIdAndArticleId(memberId, articleId);
			articleRepository.updateGoodUp(articleId);
			return 1;
		} else {
			articleLikeRepository.deleteByMemberIdAndArticleId(memberId, articleId);
			articleRepository.updateGoodDown(articleId);
			return 0;
		}
	}

	public int getLikeCount(Long id) {
		return articleRepositoryJpa.findById(id)
								   .orElseThrow(() -> new IllegalArgumentException())
								   .getGood();
	}

	public int checkBookmarkStatus(Long memberId, Long articleId) {
		Bookmark bookmark = bookmarkRepository.selectBookmarkByMemberIdAndArticleId(memberId, articleId);
		if (bookmark == null)
			return 0;
		else
			return 1;
	}

	public Map<String, Object> checkArticleSatus(Long memberId, Long articleId) {
		Map<String, Object> map = new HashMap<String, Object>();
		int liked = checkLikeStatus(memberId, articleId);
		int bookmark = checkBookmarkStatus(memberId, articleId);
		map.put("liked", liked);
		map.put("bookmark", bookmark);
		return map;
	}

	public boolean checkArticleOwner(Long memberId, ArticleDto article) {
		if (article.getMemberId().equals(memberId)) {
			return true;
		}
		return false;
	}

	public boolean checkStatusTemp(ArticleDto article) {
		if (article.getStatus().equals("TEMP")) {
			return true;
		}
		return false;
	}

	public Map<String, Object> getMainArticle() {
		//게시글 가져오기
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArticleDto> newArticles = articleRepository.selectArticleList(null, null, null, NEWEST, 0,
				MAIN_ARTICLE_NUM);
		List<ArticleDto> popularArticles = articleRepository.selectArticleList(null, null, null, POPULAR, 0,
				MAIN_ARTICLE_NUM);
		
		//이미지 가져오기
		List<UploadFile> newImages = getImages(newArticles.stream().map(a -> a.getId()).collect(Collectors.toList()));
		List<UploadFile> popularImages = getImages(popularArticles.stream().map(a -> a.getId()).collect(Collectors.toList()));

		//이미지 선택하기
		if(newImages.size()>4)	newImages = newImages.subList(0, 4);
		
		popularImages = chooseImages(newImages, popularImages);
		map.put("newest", newArticles);
		map.put("popular", popularArticles);
		map.put("newImages", newImages);
		map.put("popularImages", popularImages);

		return map;

	}

	private List<UploadFile> chooseImages(List<UploadFile> newImages, List<UploadFile> popularImages) {
		ArrayList<UploadFile> temp = new ArrayList<UploadFile>();
		int count = 0;
		for(UploadFile file : popularImages) {
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

	private List<UploadFile> getImages(List<Long> articleIds){
		if(articleIds.isEmpty()) {
			return new ArrayList<UploadFile>();
		}
		return postFileRepository.selectByArticleIds(articleIds);
	}

	@NonNull
	public Article loadTempArticleById(Long memberId, Long articleId) {
		try {
			ArticleDto article = articleRepository.selectArticleById(articleId);
			if (checkArticleOwner(memberId, article) && checkStatusTemp(article)) {
				return article;
			} else {
				return new Article();
			}
		} catch (Exception e) {
			return new Article();
		}
	}
}
