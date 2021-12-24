package project.board.util;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
    public Long memberIdConvert(HttpSession session) {
        if (session.getAttribute("memberId") != null) {
            return Long.valueOf(String.valueOf(session.getAttribute("memberId")));
        }

        return null;
    }

    @Deprecated
    public Long memberIdConvertThrow(HttpSession session) throws Exception {
        Long id = memberIdConvert(session);

        if (id == null) {
            throw new Exception("forbidden");
        }

        return id;
    }
}
