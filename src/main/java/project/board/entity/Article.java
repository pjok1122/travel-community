package project.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
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
    private String nation;

    @Column(columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'PERMANENT'")
    private String status = "PERMANENT";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

}

