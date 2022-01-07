package project.board.service.validator;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import project.board.entity.Article;
import project.board.entity.Member;

@Component
public class ArticleInquiryValidator {
    public void checkArticleOwner(@NonNull Article article, @NonNull Member requester) {
        Member owner = article.getMember();
        if (owner.getId() != requester.getId()) {
            throw new IllegalArgumentException();
        }
    }

    public void checkTempArticle(@NonNull Article article) {
        if (!article.isTempArticle()) {
            throw new IllegalArgumentException();
        }
    }

    public void checkNotTempArticle(@NonNull Article article) {
        if (article.isTempArticle()) {
            throw new IllegalArgumentException();
        }
    }
}
