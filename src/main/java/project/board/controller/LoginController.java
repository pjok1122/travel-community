package project.board.controller;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import project.board.annotation.validation.PasswordPattern;
import project.board.common.SessionContext;
import project.board.controller.validator.MemberRegisterValidator;
import project.board.entity.Member;
import project.board.service.MemberServiceV2;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberServiceV2 memberService;
    private final MemberRegisterValidator memberValidator;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberForm", new MemberRegisterForm());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@RequestAttribute SessionContext session,
                           @ModelAttribute("memberForm") @Valid MemberRegisterForm memberForm,
                           BindingResult result) {
        memberValidator.validate(memberForm, result);
        if (result.hasErrors()) {
            return "member/register";
        }
        Member member = memberService.save(memberForm.getEmail(), memberForm.getPassword());

        session.setId(member.getId());
        session.setEmail(member.getEmail());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(@RequestAttribute SessionContext session, Model model) {
        if (!session.isLoggedIn()) {
            model.addAttribute("loginForm", new LoginForm());
            return "member/login";
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestAttribute SessionContext session,
                        @ModelAttribute("loginForm") @Valid LoginForm loginForm,
                        BindingResult result) {
        if (result.hasErrors()) {
            return "member/login";
        }

        Member member = memberService.login(loginForm.getEmail(), loginForm.getPassword());
        if (member == null) {
            result.rejectValue("password", "Dismatch your infomation", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "member/login";
        }

        session.setId(member.getId());
        session.setEmail(member.getEmail());

        String prevPage = session.popPreviousPage();
        if (prevPage != null) {
            return "redirect:" + prevPage;
        }

        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(@RequestAttribute SessionContext session) {
        session.invalidate();
        return "redirect:/";
    }

    @Data
    public static class MemberRegisterForm {

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @PasswordPattern
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
