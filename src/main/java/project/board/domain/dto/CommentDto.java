package project.board.domain.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Comment;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommentDto extends Comment {
	private String writer;
	private Boolean isGood;
	
	private List<ReplyDto> replies = new ArrayList<ReplyDto>();
	
	public boolean replyEmpty()
	{
		if(replies.isEmpty())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isParent()
	{
		if(getParentCommentId() == null)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isChild()
	{
		if(getParentCommentId() != null)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean parentHaveNoChildren()
	{
		if(replyEmpty())
		{
			return true;
		}
		
		return false;
	}

}
