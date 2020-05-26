package project.board.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import project.board.domain.dto.MemberDto;
import project.board.service.MemberService;

@Component
public class MemberRegisterValidator{
	
	@Autowired
	MemberService memberService;
	
	
	public void validate(MemberDto member, Errors errors) {
		if(memberService.existMember(member)) {
			errors.rejectValue("email", "Exist email", "이미 존재하는 이메일입니다.");
			return;
		}
		
		if(!memberService.equalPassword(member)) {
			errors.rejectValue("rePassword", "Not equal password" ,"비밀번호가 일치하지 않습니다. 다시 한 번 확인해주세요.");
			return;
		}
	}
	
}
