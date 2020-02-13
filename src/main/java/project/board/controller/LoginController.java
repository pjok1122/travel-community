package project.board.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import project.board.domain.Member;
import project.board.dto.MemberDto;
import project.board.service.MemberService;


@Controller
public class LoginController {
	
	@Autowired
	MemberService memberService;
	
	
	@GetMapping("/register")
	public String getRegisterForm() {
		return "member/register";
	}
	
	@PostMapping("/register")
	public String registerMember(HttpSession session, @ModelAttribute @Valid MemberDto memberDto, BindingResult result, Model model) {
		//빈칸인 경우,
		if(result.hasErrors()) {
			model.addAttribute("error", "Not Empty");
			return "member/register";
		}
		//아이디가 있는 경우
		if(memberService.existMember(memberDto)) {
			model.addAttribute("error", "이미 존재하는 아이디입니다.");
			return "member/register";
		}
		
		//패스워드가 일치하지 않는 경우,
		if (!memberService.equalPassword(memberDto)) {
			model.addAttribute("error", "1차 비밀번호와 2차 비밀번호가 일치하지 않습니다.");
			return "member/register";
		}
		
		Member member = memberService.save(memberDto);
		session.setAttribute("memberId", member.getId());
		
		return "index";
	}

	@GetMapping("/login")
	public String getLoginForm(HttpSession session) {
		if (session.getAttribute("member") == null) {
			return "member/login";
		} else {
			return "index";
		}
	}
	
	@PostMapping("/login")
	public String login(HttpSession session, @ModelAttribute @Valid MemberDto memberDto, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("error", "Not Empty");
			return "member/login";
		}
		Member member = memberService.login(memberDto);
		
		if (member == null) { 
			model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "member/login";
		}
		else {
			session.setAttribute("memberId", member.getId());
			return "index";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "index";
	}
}
