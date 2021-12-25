package project.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "children"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private Comment parent;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int good = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> commentLikes;

    //fixme: updateDate를 삭제 날짜를 쓰고 있으므로 AuditEntity 사용 불가. deleteDate 추가 고려
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    public void goodMinusOne() {
        this.good--;
    }

    public void goodPlusOne() {
        this.good++;
    }

    public boolean parentAlreadyDeleted() {
        // updateDate가 삭제 날짜
        if (getUpdateDate() != null) {
            return true;
        }
        return false;
    }

    public void parentDelete() {
        this.updateDate = LocalDateTime.now();
    }

    public boolean isChild() {
        return parent != null && parent.getId() != null;
    }

    @Builder
    public Comment(Article article, Member member, Comment parent, String content) {
        this.article = article;
        this.member = member;
        this.parent = parent;
        this.content = content;
        this.updateDate = null;
    }
}

