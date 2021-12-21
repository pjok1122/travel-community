package project.board.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import project.board.annotation.LoginAuth;
import project.board.common.MemberRegisterValidator;
import project.board.domain.Member;
import project.board.domain.dto.MemberDto;
import project.board.service.MemberService;
import project.board.util.SessionManager;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final SessionManager sessionManager;
    private final MemberRegisterValidator memberValidator;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(HttpSession session, @ModelAttribute @Valid MemberDto memberDto,
                           BindingResult result) {
        memberValidator.validate(memberDto, result);
        if (result.hasErrors()) {
            return "member/register";
        }

        Member member = memberService.save(memberDto);
        session.setAttribute("memberId", member.getId());
        session.setAttribute("email", member.getEmail());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (sessionManager.getMemberId(session) == null) {
            model.addAttribute("memberDto", new MemberDto());
            return "member/login";
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @ModelAttribute("memberDto") @Valid MemberDto memberDto,
                        BindingResult result) {
        if (result.hasErrors()) {
            return "member/login";
        }

        Member member = memberService.login(memberDto);
        if (member == null) {
            result.rejectValue("password", "Dismatch your infomation", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "member/login";
        } else {
            session.setAttribute("memberId", member.getId());
            session.setAttribute("email", member.getEmail());

            String prevPage = (String) session.getAttribute("prevPage");
            if (prevPage != null) {
                session.removeAttribute("prevPage");
                return "redirect:" + prevPage;
            }

            return "redirect:/";
        }
    }

    @RequestMapping("/logout")
    @LoginAuth
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
