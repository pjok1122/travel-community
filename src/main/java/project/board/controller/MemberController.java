package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.domain.Article;
import project.board.domain.Comment;
import project.board.domain.Member;
import project.board.dto.MemberDto;
import project.board.enums.MyInfo;
import project.board.service.ArticleService;
import project.board.service.BookmarkService;
import project.board.service.CommentService;
import project.board.service.MemberService;

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
	
	@GetMapping("")
	public String getMypage(@RequestParam(required=false) MyInfo info, HttpSession session, Model model) {
		Long memberId = (Long)session.getAttribute("memberId");
		
		if(session.getAttribute("memberId") == null) {
			return "member/login";
		} else {
			if(info.toString().equals("MEMBER")) {
				Member member = memberService.getMyInfo(memberId);
				int like = memberService.getGoodCount(memberId);
				model.addAttribute("member", member);
				model.addAttribute("like", like);
			} else if(info.toString().equals("ARTICLE")) {
				List<Article> articleList = articleService.getArticleByMemberId(memberId);
				Long total = articleService.getArticleCntByMemberId(memberId);
				model.addAttribute("articleList", articleList);
				model.addAttribute("total", total);
			} else if(info.toString().equals("TEMP_ARTICLE")) {
				List<Article> articleList = articleService.getTempArticleByMemberId(memberId);
				Long total = articleService.getTempArticleCntByMemberId(memberId);
				model.addAttribute("articleList", articleList);
				model.addAttribute("total", total);
			} else if(info.toString().equals("BOOKMARK")) {
				List<Article> articleList = bookmarkService.getArticleByMemberId(memberId);
				Long total = bookmarkService.getTotalCntByMemberId(memberId);
				model.addAttribute("articleList", articleList);
				model.addAttribute("total", total);
			} else if(info.toString().equals("COMMENT")) {
				List<Comment> commentList = commentService.getCommentByMemberId(memberId);
				Long total = commentService.getTotalCntByMemberId(memberId);
				model.addAttribute("commentList", commentList);
				model.addAttribute("total",total);
			}
			return BASE_VIEW_NAME + info.toString().toLowerCase();
		}
	}
	@GetMapping("/auth")
//	@LoginAuth
	public String getAuthForm(HttpSession session, Model model) {
		if(session.getAttribute("memberId")==null) {
			return "/mebmer/login";
		}
		return BASE_VIEW_NAME + "auth";
	}
	
	@PostMapping("/auth")
	public String isOwner(@ModelAttribute @Valid MemberDto memberDto, BindingResult result, Model model, HttpSession session) {
		if(result.hasErrors()) {
			model.addAttribute("error", "Not Empty");
			return BASE_VIEW_NAME + "auth";
		}
		
		Member member = memberService.login(memberDto);
		
		if(member == null) {
			model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
			return BASE_VIEW_NAME + "auth";
		}
		
		session.setAttribute("isOwner", true);
		System.out.println(session.getAttribute("prevPage").toString());
		return "redirect:" + session.getAttribute("prevPage");
	}
	
	@GetMapping("/update")
	public String getMypageUpdateForm(HttpServletRequest request) {
		HttpSession session =request.getSession();
		session.setAttribute("prevPage", request.getRequestURI());
		
		if(session.getAttribute("memberId") == null) {
			return "member/login";
		}
		else if(session.getAttribute("isOwner") == null) {
			return BASE_VIEW_NAME + "auth";
		}
		else {
			return BASE_VIEW_NAME + "update";
		}
	}
	
	
	@PostMapping("/update")
	public String updateMemberInfo(HttpSession session, @ModelAttribute @Valid MemberDto memberDto, BindingResult result, Model model) {
		if(session.getAttribute("memberId") == null) {
			return "member/login";
		}
		else if(session.getAttribute("isOwner")== null) {
			return BASE_VIEW_NAME +"auth";
		}
		
		if(result.hasErrors()) {
			model.addAttribute("error", "Not Empty");
			return BASE_VIEW_NAME + "update";
		}
		
		if(!memberService.equalPassword(memberDto)) {
			model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
			return BASE_VIEW_NAME + "update";
		}
		
		memberService.update(memberDto);
		session.removeAttribute("isOwner");
		return "redirect:/mypage?info=member";	
	}
	
	@RequestMapping("/delete")
	public String deleteMember(HttpServletRequest request, HttpSession session) {
		if(session.getAttribute("memberId")==null) {
			return "member/login";
		} else if(session.getAttribute("isOwner")==null) {
			session.setAttribute("prevPage", request.getRequestURI());
			return BASE_VIEW_NAME +"auth";
		} else {
			memberService.delete((Long)session.getAttribute("memberId"));
			session.invalidate();
			return "redirect:/";
		}
	}
	
}
