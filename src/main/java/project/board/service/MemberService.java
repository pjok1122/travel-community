package project.board.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.domain.Member;
import project.board.domain.dto.MemberDto;
import project.board.domain.dto.Page;
import project.board.enums.MyInfo;
import project.board.jpa.MemberRepositoryJpa;
import project.board.repository.MemberRepository;
import project.board.util.Sha256Utils;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberRepositoryJpa memberRepositoryJpa;
	private final ArticleService articleService;
	private final BookmarkService bookmarkService;
	private final CommentService commentService;
	private final Sha256Utils sha256Utils;

	@Transactional
	public project.board.entity.Member login(String email, String password) {
		project.board.entity.Member member = memberRepositoryJpa.findByEmail(email).orElse(null);
		if (member != null && passwordCompare(password, member)) {
			member.login();
		}
		
		return member;
	}
	
	public Boolean passwordCompare(String password, project.board.entity.Member savedMember) {
		String hashPassword = sha256Utils.sha256(password, savedMember.getSalt());
		return StringUtils.equals(hashPassword, savedMember.getPassword());
	}

	public project.board.entity.Member save(String email, String password) {
		String salt = UUID.randomUUID().toString();

		String hashPassword = sha256Utils.sha256(password, salt);
		project.board.entity.Member member = project.board.entity.Member.builder()
																		.email(email)
																		.password(hashPassword)
																		.salt(salt)
																		.loginDate(LocalDateTime.now())
																		.build();
		
		return memberRepositoryJpa.save(member);
	}

	public boolean equalPassword(MemberDto memberDto) {
		if (memberDto.getPassword().equals(memberDto.getRePassword())) {
			return true;
		} else {
			return false;
		}
	}

	public void update(@Valid MemberDto memberDto) {
		if(equalPassword(memberDto)) {
			Member member = memberRepository.findByEmail(memberDto.getEmail());
			String hashPassword = sha256Utils.sha256(memberDto.getPassword(), member.getSalt());
			member.setPassword(hashPassword);
			memberRepository.updatePassword(member);
			
		}
		
	}

	private Member getMyInfo(Long id) {
		
		return memberRepository.findById(id);
	}

	private int getGoodCount(Long id) {
		Integer goodCnt = memberRepository.sumGoodCount(id);
		if (goodCnt == null) goodCnt = 0;
		return goodCnt;
	}

	public void delete(Long memberId) {
		memberRepository.delete(memberId);
	}
	
	private Map<String, Object> mypage(Long memberId){
		Member member = getMyInfo(memberId);
		int like = getGoodCount(memberId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("member", member);
		map.put("like", like);
		
		return map;
	}
	
	private Map<String, Object> myArticle(Long memberId, Page paging){
		Map<String, Object> map = new HashMap<String, Object>();
		paging.setNumberOfRecordsAndMakePageInfo(articleService.countArticle(memberId, "PERMANENT").intValue());
//		paging.setList(articleService.getArticleByMemberId(memberId, "PERMANENT", paging).getContent());
		map.put("page", paging);
		
		return map;
	}
	
	public Map<String, Object> myTempArticle(Long memberId, Page paging){
		Map<String, Object> map = new HashMap<String, Object>();
		paging.setNumberOfRecordsAndMakePageInfo(articleService.countArticle(memberId, "TEMP").intValue());
		paging.setList(articleService.getTempArticleByMemberId(memberId, paging));
		map.put("page", paging);
		
		return map;
	}
	
	private Map<String, Object> myBookmark(Long memberId, Page paging){
		Map<String, Object> map = new HashMap<String, Object>();
		paging.setNumberOfRecordsAndMakePageInfo(bookmarkService.getArticleCntByMemberId(memberId));
		paging.setList(bookmarkService.getArticleByMemberId(memberId, paging));
		map.put("page", paging);
		
		return map;
	}
	
	private Map<String, Object> myComment(Long memberId, Page paging){
		Map<String, Object> map = new HashMap<String, Object>();
		paging.setNumberOfRecordsAndMakePageInfo(commentService.getCommentCntByMemberId(memberId));
		paging.setList(commentService.getCommentByMemberId(memberId, paging));
		map.put("page", paging);
		
		return map;
	}

	
	public Map<String, Object> getMypage(MyInfo info, Long memberId, int page){
		
		Page paging = new Page(page);
		
		switch (info) {
		
			case MEMBER:
				return mypage(memberId);
				
			case ARTICLE:
				return myArticle(memberId, paging);
	
			case TEMP_ARTICLE:
				return myTempArticle(memberId, paging);
	
			case BOOKMARK:
				return myBookmark(memberId, paging);
	
			case COMMENT:
				return myComment(memberId, paging);
		}
		return null;			
	}
}