package project.board.aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import project.board.domain.dto.ArticleDto;
import project.board.service.ArticleServiceJpa;
import project.board.util.MySessionUtils;

@Component
@Aspect
@RequiredArgsConstructor
public class SessionValidAspect {
	
	private final MySessionUtils sessionUtils;
	
	@Order(1)
	@Around("@annotation(project.board.annotation.LoginAuth)")
	public String loginAuth(ProceedingJoinPoint pjp) throws Throwable {
		HttpSession session = parseSession(pjp);
		
		if(!sessionUtils.isLogined(session)) {
			return "redirect:/login";
		}
		return (String) pjp.proceed();
	}
	
	@Order(1)
	@Around("@annotation(project.board.annotation.AjaxLoginAuth)")
	public Object AjaxLoginAuth(ProceedingJoinPoint pjp) throws Throwable {
		HttpSession session = parseSession(pjp);
		
		if(!sessionUtils.isLogined(session)) {
			return ResponseEntity.status(302).body("/login");
		}
		
		return pjp.proceed();
	}
	
	private HttpSession parseSession(ProceedingJoinPoint pjp){
		HttpSession session = null;
		for(Object o : pjp.getArgs()) {
			if(o instanceof HttpSession) {
				session = (HttpSession) o;
			}
		}
		return session;
	}
}
