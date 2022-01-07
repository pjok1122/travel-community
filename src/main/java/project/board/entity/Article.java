package project.board.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import lombok.*;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.service.dto.ArticleRegisterParam;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.validator.constraints.Length;
import org.thymeleaf.util.StringUtils;

@Getter
//@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Article extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(max = 50)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Length(max = 1000000)
    private String content;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int good;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int hit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nation nation;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'PERMANENT'", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.PERMANENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column
    private long commentCount = 0L;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<ArticleLike> articleLikes = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFile> postFiles = new ArrayList<>();

    public static Article create(String title, String content, Category category, Nation nation, ArticleStatus status,
                       Member member, List<PostFile> postFiles) {
        Article article = new Article();
        article.title = StringEscapeUtils.escapeHtml4(title);
        article.content = content;
        article.category = category;
        article.nation = nation;
        article.status = status;
        article.member = member;
        article.addPostFiles(postFiles);

        return article;
    }

    public void update(String title, String content, Category category, Nation nation,
                       ArticleStatus status, List<PostFile> postFiles) {
        this.title = StringEscapeUtils.escapeHtml4(title);
        this.content = content;
        this.category = category;
        this.nation = nation;
        this.status = status;
        this.postFiles.clear();
        addPostFiles(postFiles);
    }

    public boolean isTempArticle() {
        return status == ArticleStatus.TEMP;
    }

    public void addPostFile(PostFile postFile) {
        this.postFiles.add(postFile);
        postFile.setArticle(this);
    }

    public void addPostFiles(List<PostFile> postFiles) {
        postFiles.forEach(pf-> addPostFile(pf));
    }
}

