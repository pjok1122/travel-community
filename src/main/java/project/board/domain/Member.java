package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@Alias("member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	private Long id;
	private String email;
	private String password;
	private String salt;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
	private LocalDateTime loginDate;
	
	@Default
	private String role = "USER";
}
