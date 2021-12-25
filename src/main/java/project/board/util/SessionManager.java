package project.board.util;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private static final String EMAIL = "email";
    private static final String VERIFIED = "verified";

    private static final String PREVIOUS_PAGE = "prevPage";
    private static final String HOME_PAGE = "/";

    public Long getMemberId(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }

    public Boolean isLoggedIn(HttpSession session) {
        return (session.getAttribute("memberId") != null);
    }

    public String getMemberEmail(HttpSession session) {
        return (String) session.getAttribute(EMAIL);
    }

    public String popPreviousPage(HttpSession session) {
        String prevPage = (String) session.getAttribute(PREVIOUS_PAGE);
        session.removeAttribute(PREVIOUS_PAGE);
        return prevPage != null ? prevPage : HOME_PAGE;
    }

    public void setVerified(HttpSession session, boolean isVerified) {
        session.setAttribute(VERIFIED, isVerified);
    }

	public void setPreviousPage(HttpSession session, String previousPage) {
		session.setAttribute(PREVIOUS_PAGE, previousPage);
	}

    public boolean isVerified(HttpSession session) {
        Object isVerified = session.getAttribute(VERIFIED);
        return isVerified != null && isVerified instanceof Boolean && BooleanUtils.isTrue((Boolean) isVerified);
    }
}
