//package project.board.service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import project.board.domain.Member;
//import project.board.domain.dto.MemberDto;
//import project.board.domain.dto.Page;
//import project.board.enums.MyInfo;
//import project.board.repository.MemberRepository;
//import project.board.util.Sha256Utils;
//
////@Service
//public class MemberService {
//
//	@Autowired
//	MemberRepository memberRepository;
//	
//	@Autowired
//	ArticleServiceJpa articleService;
//	
//	@Autowired
//	BookmarkServiceJpa bookmarkService;
//	
//	@Autowired
//	CommentService commentService;
//	
//	@Autowired
//	Sha256Utils sha256Utils;
//	
//	public Member login(MemberDto memberDto) {
//		System.out.println(memberDto.getEmail());
//		Member savedMember = memberRepository.findByEmail(memberDto.getEmail());
//		if (savedMember == null)
//			return null;
//		if (passwordCompare(memberDto, savedMember)) {
//			memberRepository.updateLoginDate(savedMember.getId());
//			return savedMember;
//		}
//		
//		return null;
//	}
//	
//	public Boolean passwordCompare(MemberDto memberDto, Member savedMember) {
//		String salt = savedMember.getSalt();
//		String pwd = memberDto.getPassword();
//		String hashPwd = sha256Utils.sha256(pwd, salt);
//		
//		if (hashPwd.equals(savedMember.getPassword())){
//			return true;
//		}
//		
//		return false;
//	}
//
//	public Member save(MemberDto memberDto) {
//		String salt = UUID.randomUUID().toString();
//		
//		String hashPassword = sha256Utils.sha256(memberDto.getPassword(), salt);
//		Member member = Member.builder().email(memberDto.getEmail())
//						.password(hashPassword)
//						.salt(salt).build();
//		
//		memberRepository.insert(member);
//		
//		return member;
//	}
//
//	public boolean existMember(MemberDto memberDto) {
//		Member member = memberRepository.findByEmail(memberDto.getEmail());
//		if (member==null) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public boolean equalPassword(MemberDto memberDto) {
//		if (memberDto.getPassword().equals(memberDto.getRePassword())) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public void update(@Valid MemberDto memberDto) {
//		if(equalPassword(memberDto)) {
//			Member member = memberRepository.findByEmail(memberDto.getEmail());
//			String hashPassword = sha256Utils.sha256(memberDto.getPassword(), member.getSalt());
//			member.setPassword(hashPassword);
//			memberRepository.updatePassword(member);
//			
//		}
//		
//	}
//
//	private Member getMyInfo(Long id) {
//		
//		return memberRepository.findById(id);
//	}
//
//	private int getGoodCount(Long id) {
//		Integer goodCnt = memberRepository.sumGoodCount(id);
//		if (goodCnt == null) goodCnt = 0;
//		return goodCnt;
//	}
//
//	public void delete(Long memberId) {
//		memberRepository.delete(memberId);
//	}
//	
//	private Map<String, Object> mypage(Long memberId){
//		Member member = getMyInfo(memberId);
//		int like = getGoodCount(memberId);
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("member", member);
//		map.put("like", like);
//		
//		return map;
//	}
//	
//	private Map<String, Object> myArticle(Long memberId, Page paging){
//		Map<String, Object> map = new HashMap<String, Object>();
//		paging.setNumberOfRecordsAndMakePageInfo(articleService.getArticleCntByMemberId(memberId));
//		paging.setList(articleService.getArticleByMemberId(memberId, paging));
//		map.put("page", paging);
//		
//		return map;
//	}
//	
//	public Map<String, Object> myTempArticle(Long memberId, Page paging){
//		Map<String, Object> map = new HashMap<String, Object>();
//		paging.setNumberOfRecordsAndMakePageInfo(articleService.getTempArticleCntByMemberId(memberId));
//		paging.setList(articleService.getTempArticleByMemberId(memberId, paging));
//		map.put("page", paging);
//		
//		return map;
//	}
//	
//	private Map<String, Object> myBookmark(Long memberId, Page paging){
//		Map<String, Object> map = new HashMap<String, Object>();
//		paging.setNumberOfRecordsAndMakePageInfo(bookmarkService.getArticleCntByMemberId(memberId));
//		paging.setList(bookmarkService.getArticleByMemberId(memberId, paging));
//		map.put("page", paging);
//		
//		return map;
//	}
//	
//	private Map<String, Object> myComment(Long memberId, Page paging){
//		Map<String, Object> map = new HashMap<String, Object>();
//		paging.setNumberOfRecordsAndMakePageInfo(commentService.getCommentCntByMemberId(memberId));
//		paging.setList(commentService.getCommentByMemberId(memberId, paging));
//		map.put("page", paging);
//		
//		return map;
//	}
//
//	
//	public Map<String, Object> getMypage(MyInfo info, Long memberId, int page){
//		
//		Page paging = new Page(page);
//		
//		switch (info) {
//		
//			case MEMBER:
//				return mypage(memberId);
//				
//			case ARTICLE:
//				return myArticle(memberId, paging);
//	
//			case TEMP_ARTICLE:
//				return myTempArticle(memberId, paging);
//	
//			case BOOKMARK:
//				return myBookmark(memberId, paging);
//	
//			case COMMENT:
//				return myComment(memberId, paging);
//		}
//		return null;			
//	}
//}