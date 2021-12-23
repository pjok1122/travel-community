package project.board.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Comment extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String content;

    private Long parentCommentId;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int good = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public boolean parentAlreadyDeleted() {
        // updateDate가 삭제 날짜
        if (getUpdateDate() != null) {
            return true;
        }
        return false;
    }
}

