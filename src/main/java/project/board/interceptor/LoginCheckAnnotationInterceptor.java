package project.board.interceptor;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;
import project.board.annotation.Authorization;
import project.board.common.SessionContext;

@Slf4j
@Component
public class LoginCheckAnnotationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Authorization annotation = getAnnotation(handlerMethod, Authorization.class);
        if (annotation != null) {
            SessionContext session = (SessionContext) request.getAttribute("session");
            if (session == null || session.getId() == null) {
                log.info("[INVESTIGATION_AUTH_FAIL]: {}", request);

                String ajaxHeader = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(ajaxHeader)) {
                    // AJAX 요청 시에
                    response.sendError(401, request.getContextPath() + "/login");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
                return false;
            }
        }

        //TODO: Admin만 할 수 있는 요청 검증 필요.
        return true;

    }

    private <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotation) {
        return Optional.ofNullable(handlerMethod.getMethodAnnotation(annotation))
                       .orElse(handlerMethod.getBeanType().getAnnotation(annotation));
    }
}
