package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Alias("member")
@Builder
public class Member {
	private Long id;
	private String email;
	private String password;
	private String salt;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
	private LocalDateTime loginDate;
	private String role;
}
