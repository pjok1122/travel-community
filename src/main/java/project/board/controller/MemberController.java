package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.LoginAuth;
import project.board.common.MemberRegisterValidator;
import project.board.common.MemberUpdateValidator;
import project.board.domain.Article;
import project.board.domain.Comment;
import project.board.domain.Member;
import project.board.domain.dto.MemberDto;
import project.board.enums.MyInfo;
import project.board.service.ArticleService;
import project.board.service.BookmarkService;
import project.board.service.CommentService;
import project.board.service.MemberService;
import project.board.util.MySessionUtils;

@Controller
@RequestMapping("/mypage")
public class MemberController {
	
	private static final String BASE_VIEW_NAME = "member/mypage/";

	@Autowired
	MemberService memberService;
	
	@Autowired
	ArticleService articleService;
	
	@Autowired
	BookmarkService bookmarkService;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	MySessionUtils sessionUtils;
	
	@Autowired
	MemberUpdateValidator memberValidator;
	
	@GetMapping("/{info}")
	@LoginAuth
	public String getMypage(
		@PathVariable(required = true, value = "info") MyInfo info,
		@RequestParam(required = false, value = "page", defaultValue = "1") int page, 
		HttpSession session, 
		Model model)
	{
		Long memberId = (Long)session.getAttribute("memberId");
		model.addAllAttributes(memberService.getMypage(info, memberId, page));
		
		return BASE_VIEW_NAME + info.toString().toLowerCase();
	}
	@GetMapping("/auth")
	@LoginAuth
	public String getAuthForm(HttpSession session, Model model) {
		model.addAttribute(MemberDto.builder().email(sessionUtils.getMemberEmail(session)).build());
		return BASE_VIEW_NAME + "auth";
	}
	
	@PostMapping("/auth")
	@LoginAuth
	public String isOwner(
			@ModelAttribute("memberDto") @Valid MemberDto memberDto, 
			BindingResult result, 
			Model model, 
			HttpSession session)
	{
		if(result.hasErrors()) {
			return BASE_VIEW_NAME + "auth";
		}
		
		if(memberService.login(memberDto) == null) {
			result.rejectValue("rePassword", "dismatch your info", "비밀번호가 일치하지 않습니다.");
			return BASE_VIEW_NAME + "auth";
		}
		
		session.setAttribute("isOwner", true);
		return "redirect:" + session.getAttribute("prevPage");
	}
	
	@GetMapping("/update")
	@LoginAuth
	public String getUpdateMypageForm(
			HttpServletRequest request,
			Model model,
			HttpSession session)
	{
		session.setAttribute("prevPage", request.getRequestURI());
		model.addAttribute(MemberDto.builder().email(sessionUtils.getMemberEmail(session)).build());
		
		if(session.getAttribute("isOwner") == null) {
			return BASE_VIEW_NAME + "auth";
		}
		else {
			return BASE_VIEW_NAME + "update";
		}
	}
	
	@PostMapping("/update")
	@LoginAuth
	public String processUpdateMemberForm(
			@ModelAttribute @Valid MemberDto memberDto,
			BindingResult result,
			HttpSession session,
			Model model) {
		if(session.getAttribute("isOwner")== null) {
			model.addAttribute(MemberDto.builder().email(sessionUtils.getMemberEmail(session)).build());
			return BASE_VIEW_NAME +"auth";
		}
		
		memberValidator.validate(memberDto, result);
		if(result.hasErrors()) {
			model.addAttribute(memberDto);
			return BASE_VIEW_NAME + "update";
		}
		
		memberService.update(memberDto);
		session.removeAttribute("isOwner");
		return "redirect:/mypage/member";	
	}
	
	@RequestMapping("/delete")
	@LoginAuth
	public String deleteMember(
			HttpServletRequest request,
			Model model,
			HttpSession session) 
	{
		if(session.getAttribute("isOwner")==null) {
			session.setAttribute("prevPage", request.getRequestURI());
			model.addAttribute(MemberDto.builder().email(sessionUtils.getMemberEmail(session)).build());
			return BASE_VIEW_NAME +"auth";
		} else {
			memberService.delete(sessionUtils.getMemberId(session));
			session.invalidate();
			return "redirect:/";
		}
	}
	
}
