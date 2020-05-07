package project.board.entity.dto;

import java.time.LocalDateTime;

import lombok.Data;
import project.board.entity.Member;

@Data
public class MyInfoDto {

	private String email;
	private LocalDateTime createdDate;
	private LocalDateTime loginDate;
	private int likeCount;
	
	public MyInfoDto(Member member, int likeCount) {
		email = member.getEmail();
		createdDate = member.getCreatedDate(); 
		loginDate = member.getLoginDate();
		this.likeCount = likeCount;
	}
	
}
