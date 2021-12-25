package project.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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

    @NotBlank
    @Column(nullable = false)
    private String nation;      //TODO : change enum after refactoring articleService

    @Column(columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'PERMANENT'")
    private String status = "PERMANENT";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}

