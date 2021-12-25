package project.board.controller.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.controller.LoginController.MemberRegisterForm;
import project.board.jpa.MemberRepositoryJpa;

@Component
@RequiredArgsConstructor
public class MemberRegisterValidator{

	private final MemberRepositoryJpa memberRepository;
	
	
	public void validate(MemberRegisterForm member, Errors errors) {
		memberRepository.findByEmail(member.getEmail())
						.ifPresent(m -> errors.rejectValue("email", "Exist email", "이미 존재하는 이메일입니다."));

		if (!StringUtils.equals(member.getPassword(), member.getRePassword())) {
			errors.rejectValue("rePassword", "Not equal password" ,"비밀번호가 일치하지 않습니다. 다시 한 번 확인해주세요.");
		}
		
	}
	
}
