package project.board.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import project.board.domain.dto.MemberDto;
import project.board.service.MemberService;

@Component
public class MemberUpdateValidator{
	
	@Autowired
	MemberService memberService;
	
	public void validate(MemberDto member, Errors errors) {
		if(!memberService.equalPassword(member)) {
			errors.rejectValue("rePassword", "Not equal password" ,"비밀번호가 일치하지 않습니다. 다시 한 번 확인해주세요.");
			return;
		}		
		
	}
	
}
