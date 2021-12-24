package project.board.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {
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

    public void goodMinusOne() {
        this.good--;
    }

    public void goodPlusOne() {
        this.good++;
    }

    //fixme: updateDate를 삭제 날짜를 쓰고 있으므로 AuditEntity 사용 불가. deleteDate 추가 고려
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public boolean parentAlreadyDeleted() {
        // updateDate가 삭제 날짜
        if (getUpdateDate() != null) {
            return true;
        }
        return false;
    }

    public boolean isChild() {
        return getParentCommentId() != null;
    }

    @Builder
    public Comment(Article article, Member member, Long parentCommentId, String content) {
        this.article = article;
        this.member = member;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.updateDate = null;
    }
}

