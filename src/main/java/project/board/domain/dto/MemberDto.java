package project.board.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.apache.ibatis.type.Alias;

import com.sun.istack.internal.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Alias("memberDto")
public class MemberDto {

	@NotBlank @Email
	private String email;
		
	@NotBlank
	private String password;
	
	private String rePassword;
}
