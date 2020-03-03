package project.board.domain.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Comment;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommentDto extends Comment{
	private String writer;
	
//	private List<Comment> replies = new ArrayList<Comment>();

}
