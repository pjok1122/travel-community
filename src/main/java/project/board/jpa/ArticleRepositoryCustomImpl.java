package project.board.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.QArticle;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.params.ArticleSearchParam;

@RequiredArgsConstructor
@Repository
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {

    private static final QArticle article = QArticle.article;

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Article> searchOrderByNewest(ArticleSearchParam searchParam) {
        QueryResults<Article> result = queryFactory.selectFrom(article)
                                                   .where(eqCategory(searchParam.getCategory()),
                                                          eqNation(searchParam.getNation()),
                                                          eqStatus(searchParam.getStatus()),
                                                          containsTitle(
                                                                  searchParam.getSearchText()))
                                                   .offset(searchParam.getPageable().getOffset())
                                                   .limit(searchParam.getPageable().getPageSize())
                                                   .orderBy(article.registerDate.desc())
                                                   .fetchResults();

        return new PageImpl<>(result.getResults(), searchParam.getPageable(), result.getTotal());
    }

    @Override
    public Page<Article> searchOrderByPopular(ArticleSearchParam searchParam) {
        QueryResults<Article> result = queryFactory.selectFrom(article)
                                                   .where(eqCategory(searchParam.getCategory()),
                                                          eqNation(searchParam.getNation()),
                                                          eqStatus(searchParam.getStatus()),
                                                          containsTitle(
                                                                  searchParam.getSearchText()))
                                                   .offset(searchParam.getPageable().getOffset())
                                                   .limit(searchParam.getPageable().getPageSize())
                                                   .orderBy(article.hit.add(article.good.multiply(3)).desc())
                                                   .fetchResults();

        return new PageImpl<>(result.getResults(), searchParam.getPageable(), result.getTotal());
    }

    private BooleanExpression eqCategory(Category category) {
        if (category != null && category != Category.ALL) {
            return article.category.eq(category);
        }
        return null;
    }

    private BooleanExpression eqNation(Nation nation) {
        if (nation != null && nation != Nation.ALL) {
            return article.nation.eq(nation);
        }
        return null;
    }

    private BooleanExpression containsTitle(String searchText) {
        if (StringUtils.isNotBlank(searchText)) {
            article.title.contains(searchText);
        }
        return null;
    }

    private BooleanExpression eqStatus(ArticleStatus status) {
        return status == null ? null : article.status.eq(status);
    }

}
