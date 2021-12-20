package project.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import project.board.service.ArticleService;

@Controller
@RequiredArgsConstructor
public class MainController {
	private final ArticleService articleService;
	
	@GetMapping()
	public String main(Model model) {
		model.addAllAttributes(articleService.getMainArticle());
		return "index";
	}
}
