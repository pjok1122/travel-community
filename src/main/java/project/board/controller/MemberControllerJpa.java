package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.dto.MyInfoDto;
import project.board.repository.MemberRepositoryJpa;
import project.board.service.MemberServiceJpa;
import project.board.util.MySessionUtils;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MemberControllerJpa {
	
	private static final String BASE_VIEW_NAME = "member/mypage/";
	
	private final MemberServiceJpa memberService;
	private final MySessionUtils sessionUtils;
	private final MemberRepositoryJpa memberRepository;
	
	@GetMapping("/member")
	public String getMyPage(HttpSession session, Model model) {
		Long memberId = sessionUtils.getMemberId(session);
		model.addAttribute("myinfo", memberService.getMyInfo(memberId));
		
		return BASE_VIEW_NAME + "member";
	}
}
