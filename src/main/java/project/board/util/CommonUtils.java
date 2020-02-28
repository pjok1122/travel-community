package project.board.util;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class CommonUtils 
{
	public Long memberIdConvert(HttpSession session)
	{
		return Long.valueOf(String.valueOf(session.getAttribute("memberId")));
	}
}
