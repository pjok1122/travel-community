package project.board.repository;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import project.board.domain.Article;
import project.board.domain.Comment;
import project.board.domain.Member;
import project.board.enums.Category;

import javax.sql.DataSource;

class CreateDomain {
    static int seq = 1;

    public static Article getArticle(Member member) {
        return Article.builder()
                .title("title")
                .memberId(member.getId())
                .category(Category.ALL)
                .content("content")
                .nation("nation")
                .build();
    }

    public static Comment getComment(Article article, Member member) {
        Comment comment = new Comment();
        comment.setArticleId(article.getId());
        comment.setMemberId(member.getId());
        comment.setContent("content");

        return comment;
    }

    public static Member getMember() {
        return Member.builder()
                .email("test" + seq++ + "@email.com")
                .password("passwd")
                .salt("salt")
                .build();
    }
}