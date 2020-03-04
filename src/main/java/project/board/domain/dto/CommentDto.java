package project.board.domain.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Comment;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommentDto extends Comment
{
	private String writer;
	
	private List<ReplyDto> replies = new ArrayList<ReplyDto>();
	
	public boolean replyEmpty()
	{
		if(replies.isEmpty())
		{
			return true;
		}
		
		return false;
	}

}
