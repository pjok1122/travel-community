package project.board.controller;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.dto.LoginForm;
import project.board.entity.dto.RegisterForm;
import project.board.repository.MemberRepositoryJpa;
import project.board.service.MemberServiceJpa;
import project.board.service.validator.MemberRegisterValidator;

@Controller
@RequiredArgsConstructor
public class LoginControllerJpa {
	
	private final MemberServiceJpa memberService;
	private final MemberRepositoryJpa memberRepository;
	private final MemberRegisterValidator registerValidator;
	
	@GetMapping("/register")
	public String getRegisterForm(@ModelAttribute("registerForm") RegisterForm registerForm) {
		return "member/register";
	}
	
	@GetMapping("/login")
	public String getLoginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
		return "member/login";
	}
	
	@PostMapping("/register")
	public String processRegisterForm(
			HttpSession session,
			@ModelAttribute("registerForm") @Valid RegisterForm registerForm,
			BindingResult result,
			Model model)
	{
		registerValidator.validate(registerForm, result);
		if(result.hasErrors()) {
			return "member/register";
		}
		
		//멤버 저장.
		Long id = memberService.save(registerForm.getEmail(), registerForm.getPassword());
		Member member = memberRepository.findById(id).orElse(null);
		session.setAttribute("memberId", member.getId());
		session.setAttribute("email", member.getEmail());
		return "redirect:/";
	}
	
	@PostMapping("/login")
	public String processLoginForm(
			HttpSession session,
			@ModelAttribute("loginForm") @Valid LoginForm loginForm,
			BindingResult result,
			Model model)
	{
		if(result.hasErrors()) return "member/login";
		
		Member member = memberService.login(loginForm.getEmail(), loginForm.getPassword());
		if(member == null) {
			result.rejectValue("password", "Dismatch email or password", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "member/login";
		}
		
		session.setAttribute("memberId", member.getId());
		session.setAttribute("email", member.getEmail());
		
		return "redirect:/";
	}
	
	@RequestMapping("/logout")
	public String processLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
}