package project.board.aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SessionValidAspect {
	
	@Around("@annotation(project.board.annotation.LoginAuth)")
	public String loginAuth(ProceedingJoinPoint pjp) throws Throwable {
		HttpSession session = null;
		for(Object o : pjp.getArgs()) {
			if(o instanceof HttpSession) {
				session = (HttpSession) o;
			}
		}
		
		if(session.getAttribute("memberId")==null) {
			return "member/login";
		}
		return (String) pjp.proceed();
	}
}
