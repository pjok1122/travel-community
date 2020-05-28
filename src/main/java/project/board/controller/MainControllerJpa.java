package project.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import project.board.service.ArticleServiceJpa;

@Controller
public class MainControllerJpa {
	
	@Autowired
	ArticleServiceJpa articleService;
	
	@GetMapping("/")
	public String getMainPage(Model model) {
		model.addAllAttributes(articleService.getMainArticle());
		return "index";
	}
}
