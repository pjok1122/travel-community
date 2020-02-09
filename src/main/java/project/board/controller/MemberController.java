package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import project.board.domain.Member;
import project.board.dto.MemberDto;
import project.board.service.MemberService;


@Controller
public class MemberController {
	
	@Autowired
	MemberService memberService;
	
	@GetMapping("/login")
	public String getLogin() {
		return "member/login";
	}
	
	@PostMapping("/login")
	public String postLogin(@RequestBody MemberDto memberDto, Model model, HttpSession httpSession) {
		Member member = memberService.login(memberDto);
		
		if (member == null) { 
			model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "member/login";
		}
		else {
			httpSession.setAttribute("member", member);
			return "redirect:/index";
		}
	}
}
