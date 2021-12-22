package project.board.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import project.board.domain.Article;
import project.board.domain.Member;
import project.board.domain.dto.CommentDto;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    DataSource dataSource;

    public Member savedMember;
    public Article savedArticle;

    @BeforeEach
    void beforeEach() {
        savedMember = CreateDomain.getMember();
        memberRepository.insert(savedMember);

        savedArticle = CreateDomain.getArticle(savedMember);
        articleRepository.insertArticle(savedArticle);
    }

    @Test
    void selectCommentByArticleId_default() {
        commentRepository.insertComment(savedArticle.getId(), savedMember.getId(), null, "comment1");

        List<CommentDto> result = commentRepository.selectCommentByArticleId(savedMember.getId(), savedArticle.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getReplies().size()).isEqualTo(0);
        assertThat(result.get(0).getWriter()).isEqualTo(savedMember.getEmail());
    }

    @Test
    void selectCommentByArticleId_reply() {
        Member memberReply = CreateDomain.getMember();
        memberRepository.insert(memberReply);
        commentRepository.insertComment(savedArticle.getId(), savedMember.getId(), null, "comment1");
        List<CommentDto> comments = commentRepository.selectCommentByArticleId(savedMember.getId(), savedArticle.getId());
        commentRepository.insertComment(savedArticle.getId(), memberReply.getId(), comments.get(0).getId(), "reply");

        List<CommentDto> result = commentRepository.selectCommentByArticleId(savedMember.getId(), savedArticle.getId());

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getReplies().size()).isEqualTo(1);
        assertThat(result.get(0).getIsGood()).isEqualTo(0);
        assertThat(result.get(0).getReplies().get(0).getWriter()).isEqualTo(memberReply.getEmail());
    }
}