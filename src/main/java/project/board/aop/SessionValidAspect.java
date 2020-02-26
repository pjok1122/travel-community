package project.board.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import project.board.domain.dto.ArticleDto;
import project.board.repository.ArticleRepository;

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
			return "redirect:/login";
		}
		return (String) pjp.proceed();
	}
}
