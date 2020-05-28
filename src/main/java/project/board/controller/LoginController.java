//package project.board.controller;
//
//import javax.servlet.http.HttpSession;
//import javax.validation.Valid;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//import project.board.annotation.LoginAuth;
//import project.board.common.MemberRegisterValidator;
//import project.board.domain.Member;
//import project.board.domain.dto.MemberDto;
//import project.board.service.MemberService;
//import project.board.util.MySessionUtils;
//
//
//@Controller
//public class LoginController {
//	
//	@Autowired
//	MemberService memberService;
//	
//	@Autowired
//	MySessionUtils sessionUtils;
//	
//	@Autowired
//	MemberRegisterValidator memberValidator;
//		
//	@GetMapping("/register")
//	public String getRegisterForm(Model model){
//		model.addAttribute("memberDto", new MemberDto());
//		return "member/register";
//	}
//	
//	@PostMapping("/register")
//	public String processRegisterForm(
//			HttpSession session,
//			@ModelAttribute @Valid MemberDto memberDto,
//			BindingResult result, 
//			Model model)
//	{
//		memberValidator.validate(memberDto, result);
//		if(result.hasErrors()) {
//			return "member/register";
//		}
//		
//		Member member = memberService.save(memberDto);
//		session.setAttribute("memberId", member.getId());
//		session.setAttribute("email", member.getEmail());
//		return "redirect:/";
//	}
//
//	@GetMapping("/login")
//	public String getLoginForm(
//			HttpSession session,
//			Model model) 
//	{
//		if(sessionUtils.getMemberId(session) == null) {
//			model.addAttribute("memberDto", new MemberDto());
//			return "member/login";
//		}
//		return "redirect:/";
//	}
//	
//	@PostMapping("/login")
//	public String processLoginForm(
//			HttpSession session,
//			@ModelAttribute("memberDto") @Valid MemberDto memberDto,
//			BindingResult result, 
//			Model model)
//	{
//		if (result.hasErrors()) {
//			return "member/login";
//		}
//
//		Member member = memberService.login(memberDto);
//		if (member == null) { 
//			result.rejectValue("password", "Dismatch your infomation", "이메일 또는 비밀번호가 일치하지 않습니다.");
//			return "member/login";
//		}
//		
//		else {
//			session.setAttribute("memberId", member.getId());
//			session.setAttribute("email", member.getEmail());
//			
//			String prevPage = (String)session.getAttribute("prevPage");
//			if(prevPage !=null) {
//				session.removeAttribute("prevPage");
//				return "redirect:" + prevPage;
//			}
//			
//			return "redirect:/";
//		}
//	}
//	
//	@RequestMapping("/logout")
//	@LoginAuth
//	public String processLogout(HttpSession session) {
//		session.invalidate();
//		return "redirect:/";
//	}
//	
//	@GetMapping("/ajax/login_check")
//	public ResponseEntity<?> processLoginCheck(HttpSession session){
//		if(session.getAttribute("memberId") ==null) {
//			return ResponseEntity.status(302).body("/login");
//		} else {
//			return ResponseEntity.ok().body(session.getAttribute("email"));
//		}
//	}
//	
//	
//	
//}
