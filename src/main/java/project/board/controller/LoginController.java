package project.board.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import project.board.annotation.LoginAuth;
import project.board.controller.validator.MemberRegisterValidator;
import project.board.entity.Member;
import project.board.service.MemberServiceV2;
import project.board.util.SessionManager;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberServiceV2 memberService;
    private final SessionManager sessionManager;
    private final MemberRegisterValidator memberValidator;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberForm", new MemberRegisterForm());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(HttpSession session, @ModelAttribute("memberForm") @Valid MemberRegisterForm memberForm,
                           BindingResult result) {
        memberValidator.validate(memberForm, result);
        if (result.hasErrors()) {
            return "member/register";
        }
        Member member = memberService.save(memberForm.getEmail(), memberForm.getPassword());
        session.setAttribute("memberId", member.getId());
        session.setAttribute("email", member.getEmail());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (sessionManager.getMemberId(session) == null) {
            model.addAttribute("loginForm", new LoginForm());
            return "member/login";
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @ModelAttribute("loginForm") @Valid LoginForm loginForm,
                        BindingResult result) {
        if (result.hasErrors()) {
            return "member/login";
        }

        Member member = memberService.login(loginForm.getEmail(), loginForm.getPassword());
        if (member == null) {
            result.rejectValue("password", "Dismatch your infomation", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "member/login";
        }

        session.setAttribute("memberId", member.getId());
        session.setAttribute("email", member.getEmail());

        String prevPage = (String) session.getAttribute("prevPage");
        if (prevPage != null) {
            session.removeAttribute("prevPage");
            return "redirect:" + prevPage;
        }

        return "redirect:/";
    }

    @RequestMapping("/logout")
    @LoginAuth
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @Data
    public static class MemberRegisterForm {

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{12,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 12자 ~ 20자의 비밀번호여야 합니다.")
        private String password;
        private String rePassword;
    }

    @Data
    public static class LoginForm {

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }

}
