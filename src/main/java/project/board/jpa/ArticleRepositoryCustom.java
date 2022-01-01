package project.board.jpa;

import org.springframework.data.domain.Page;

import project.board.entity.Article;
import project.board.params.ArticleSearchParam;

public interface ArticleRepositoryCustom {
    Page<Article> searchOrderByNewest(ArticleSearchParam searchParam);
    Page<Article> searchOrderByPopular(ArticleSearchParam searchParam);
}
