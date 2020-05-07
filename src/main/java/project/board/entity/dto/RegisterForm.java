package project.board.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


import lombok.Data;

@Data
public class RegisterForm {

	@NotBlank @Email
	private String email;
		
	@NotBlank
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{12,20}",
    message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 12자 ~ 20자의 비밀번호여야 합니다.")
	private String password;
	private String rePassword;
	
	public Boolean isValidPassword() {
		return password.equals(rePassword) ? true : false ;
	}
}

