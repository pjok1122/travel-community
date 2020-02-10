package project.board.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.sun.istack.internal.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

	@NotBlank @Email
	private String email;
	
	@NotBlank
	private String password;
	
	private String rePassword;
}
