package project.board.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.TempArticle;
import project.board.entity.UploadFile;
import project.board.entity.dto.ArticleForm;
import project.board.exception.EntityOwnerMismatchException;
import project.board.exception.ExceedTempArticleLimitException;
import project.board.exception.NoExistException;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.TempArticleRepositoryJpa;
import project.board.repository.UploadFileRepositoryJpa;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TempArticleServiceJpa {

	private final TempArticleRepositoryJpa tempArticleRepository;
	private final MemberRepositoryJpa memberRepository;
	private final UploadFileRepositoryJpa uploadFileRepository;
	private final ImageServiceJpa imageService;
	private final int TEMP_ARTICLE_MAX_SIZE = 10;

	
	/**
	 * memberId가 articleId에 해당하는 글의 작성자인지 확인하고, 작성자라면 게시물을 조회한다.
	 * @param memberId
	 * @param articleId
	 * @return tempArticle
	 */
	public TempArticle getMyArticle(Long memberId, Long articleId) {
		//게시물이 없는 경우 예외 발생
		TempArticle article = tempArticleRepository.findDetailById(articleId).orElseThrow(()-> new NoExistException("doesn't exist this article. id=" + articleId));
		//주인이 아닌 경우 예외 발생
		if(!isArticleOwner(memberId, article)) throw new EntityOwnerMismatchException();
		
		return article;
	}
	
	private Boolean isArticleOwner(Long memberId, TempArticle article) {
		return article.getMember().getId().equals(memberId) ? true : false;
	}
	
	
	/**
	 * 임시 저장글의 한도를 초과했는지 확인한다.
	 * @param memberId
	 * @return 임시 저장글을 작성할 수 있으면 true 아닐 경우 false를 반환한다.
	 */
	public boolean isWritable(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException("doesn't exist such member " + memberId));
		if(tempArticleRepository.countByMember(member) < TEMP_ARTICLE_MAX_SIZE) return true;
		return false;
	}

	/**
	 * 게시물을 저장한다.
	 * @param memberId
	 * @param articleForm
	 * @return articleId
	 */
	@Transactional
	public Long save(Long memberId, @Valid ArticleForm form) {
		if(!isWritable(memberId)) {
			throw new ExceedTempArticleLimitException();
		}
		
		//엔티티 조회
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException("Doesn't exist "+ memberId));
		List<UploadFile> images = imageService.strToUploadFiles(form.getImages());
		
		//엔티티 생성
		TempArticle article = new TempArticle(member, form.getCategory(), form.getTitle(), form.getContent(), form.getNation());

		//엔티티 저장
		tempArticleRepository.save(article);
		article.addImages(images);
		
		return article.getId();
	}

	/**
	 * 회원이 작성한 게시물을 조회한다. (Lazy Loading)
	 * @param memberId
	 * @return TempArticle을 제외한 나머지 엔티티들은 프록시 객체로 조회한다.
	 */
	public List<TempArticle> getMyArticles(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException("Doesn't exist "+ memberId));
		List<TempArticle> articles = tempArticleRepository.findByMember(member);
		return articles;
	}
	
	/**
	 * 회원이 작성한 임시 저장글을 삭제한다.
	 * 게시물의 주인이 아닐 경우, EntityOwnerMismatchException 예외가 발생한다.
	 * @param memberId
	 * @param articleId
	 */
	@Transactional
	public void remove(Long memberId, Long articleId) {
		TempArticle myArticle = getMyArticle(memberId, articleId);
		tempArticleRepository.delete(myArticle);
	}

	
	/**
	 * 회원이 작성한 임시 저장글을 수정한다.
	 * 게시물의 주인이 아닐 경우, EntityOwnerMismatchException 예외가 발생한다.
	 * @param memberId
	 * @param articleForm
	 * @return articleId
	 */
	@Transactional
	public Long update(Long memberId, @Valid ArticleForm form) {
		TempArticle oldArticle = getMyArticle(memberId, form.getArticleId());
		List<UploadFile> images = imageService.strToUploadFiles(form.getImages());
		oldArticle.getUploadFiles().clear();
		oldArticle.update(form.getTitle(), form.getContent(), form.getCategory(), form.getNation(), images);
		return oldArticle.getId();
	}
	
}
