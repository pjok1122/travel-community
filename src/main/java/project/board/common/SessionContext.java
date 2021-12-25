package project.board.common;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;

public class SessionContext {
    private static final String EMAIL = "email";
    private static final String VERIFIED = "verified";
    private static final String ID = "memberId";

    private static final String PREVIOUS_PAGE = "prevPage";
    private static final String HOME_PAGE = "/";

    private HttpSession session;

    public SessionContext(HttpSession session) {
        this.session = session;
    }

    public Long getId() {
        return (Long) session.getAttribute(ID);
    }

    public void setId(Long memberId) {
        session.setAttribute(ID, memberId);
    }

    public Boolean isLoggedIn() {
        return getId() != null;
    }

    public String getEmail() {
        return (String) session.getAttribute(EMAIL);
    }

    public void setEmail(String email) {
        session.setAttribute(EMAIL, email);
    }

    public String popPreviousPage() {
        String prevPage = (String) session.getAttribute(PREVIOUS_PAGE);
        session.removeAttribute(PREVIOUS_PAGE);
        return prevPage != null ? prevPage : HOME_PAGE;
    }

    public void setVerified(boolean isVerified) {
        session.setAttribute(VERIFIED, isVerified);
    }

    public void setPreviousPage(String previousPage) {
        session.setAttribute(PREVIOUS_PAGE, previousPage);
    }

    public boolean isVerified() {
        Object isVerified = session.getAttribute(VERIFIED);
        return isVerified != null && isVerified instanceof Boolean && BooleanUtils.isTrue((Boolean) isVerified);
    }

    public void invalidate() {
        session.invalidate();
    }
}
