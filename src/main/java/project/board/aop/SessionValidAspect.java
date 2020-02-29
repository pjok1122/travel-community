package project.board.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import project.board.domain.dto.ArticleDto;
import project.board.repository.ArticleRepository;
import project.board.service.ArticleService;
import project.board.util.MySessionUtils;

@Component
@Aspect
public class SessionValidAspect {
	
	@Autowired
	MySessionUtils sessionUtils;
	
	@Autowired
	ArticleService articleService;
	
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
	@Order(2)
	@Around("@annotation(project.board.annotation.isArticleOwner)")
	public Object isArticleOwner(ProceedingJoinPoint pjp) throws Throwable {
		HttpSession session = parseSession(pjp);
		Long memberId = sessionUtils.getMemberId(session);
		Long articleId = parseLong(pjp);
		
		ArticleDto article = articleService.getArticleById(articleId);
		if(!articleService.checkArticleOwner(memberId, article)) {
			throw new Exception("you are not a owner of the article.");
		} else {
			return pjp.proceed();
		}
		
	}
	
	private Long parseLong(ProceedingJoinPoint pjp) {
		Long articleId = null;
		for(Object o : pjp.getArgs()) {
			if(o instanceof Long) {
				articleId = (Long) o;
			}
		}
		return articleId;
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
