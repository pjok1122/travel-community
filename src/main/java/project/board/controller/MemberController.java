package project.board.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import project.board.annotation.LoginAuth;
import project.board.common.MemberUpdateValidator;
import project.board.domain.dto.MemberDto;
import project.board.enums.MyInfo;
import project.board.service.MemberService;
import project.board.util.SessionManager;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MemberController {

    private static final String BASE_VIEW_NAME = "member/mypage/";

    private final MemberService memberService;
    private final SessionManager sessionManager;
    private final MemberUpdateValidator memberValidator;

    @GetMapping("/{info}")
    @LoginAuth
    public String mypage(@PathVariable("info") MyInfo info,
                         @RequestParam(required = false, value = "page", defaultValue = "1") int page,
                         HttpSession session,
                         Model model) {
        Long memberId = sessionManager.getMemberId(session);
        model.addAllAttributes(memberService.getMypage(info, memberId, page));

        return BASE_VIEW_NAME + info.toString().toLowerCase();
    }

    @GetMapping("/auth")
    @LoginAuth
    public String authForm(HttpSession session, Model model) {
        model.addAttribute(MemberDto.builder().email(sessionManager.getMemberEmail(session)).build());
        return BASE_VIEW_NAME + "auth";
    }

    @PostMapping("/auth")
    @LoginAuth
    public String isOwner(@ModelAttribute("memberDto") @Valid MemberDto memberDto,
                          BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return BASE_VIEW_NAME + "auth";
        }

        if (memberService.login(memberDto.getEmail(), memberDto.getPassword()) == null) {
            result.rejectValue("rePassword", "dismatch your info", "비밀번호가 일치하지 않습니다.");
            return BASE_VIEW_NAME + "auth";
        }

        session.setAttribute("isOwner", true);
        return "redirect:" + session.getAttribute("prevPage");
    }

    @GetMapping("/update")
    @LoginAuth
    public String updateForm(HttpServletRequest request, Model model, HttpSession session) {
        session.setAttribute("prevPage", request.getRequestURI());
        model.addAttribute(MemberDto.builder().email(sessionManager.getMemberEmail(session)).build());

        if (session.getAttribute("isOwner") == null) {
            return BASE_VIEW_NAME + "auth";
        } else {
            return BASE_VIEW_NAME + "update";
        }
    }

    @PostMapping("/update")
    @LoginAuth
    public String update(@ModelAttribute @Valid MemberDto memberDto, BindingResult result,
                         HttpSession session) {
        if (session.getAttribute("isOwner") == null) {
            return BASE_VIEW_NAME + "auth";
        }

        memberValidator.validate(memberDto, result);
        if (result.hasErrors()) {
            return BASE_VIEW_NAME + "update";
        }

        memberService.update(memberDto);
        session.removeAttribute("isOwner");
        return "redirect:/mypage/member";
    }

    @RequestMapping("/delete")
    @LoginAuth
    public String delete(HttpServletRequest request, Model model, HttpSession session) {
        if (session.getAttribute("isOwner") == null) {
            session.setAttribute("prevPage", request.getRequestURI());
            model.addAttribute(MemberDto.builder().email(sessionManager.getMemberEmail(session)).build());
            return BASE_VIEW_NAME + "auth";
        } else {
            memberService.delete(sessionManager.getMemberId(session));
            session.invalidate();
            return "redirect:/";
        }
    }
}
