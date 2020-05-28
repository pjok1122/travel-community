package project.board.service.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.dto.RegisterForm;
import project.board.repository.MemberRepositoryJpa;

@Component
@RequiredArgsConstructor
public class MemberRegisterValidator{
	
	private final MemberRepositoryJpa memberRepository;
	
	public void validate(RegisterForm registerForm, Errors errors) {
		
		//패스워드 검증
		if(!registerForm.isValidPassword()) {
			errors.rejectValue("rePassword", "Password mismatch", "패스워드가 일치하지 않습니다.");
		}
		
		//이메일 검증
		Optional<Member> findMember = memberRepository.findByEmail(registerForm.getEmail());
		if(findMember.isPresent()) {
			errors.rejectValue("email", "Already exist email", "이미 존재하는 이메일입니다.");
		}
	}
}
