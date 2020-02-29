package project.board.util;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class MySessionUtils {
	public Long getMemberId(HttpSession session) {
		return (Long) session.getAttribute("memberId");
	}
	
	public Boolean isLogined(HttpSession session) {
		return (session.getAttribute("memberId")!=null);
	}
}
