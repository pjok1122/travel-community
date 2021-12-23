package project.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Report extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String target;

    @Column(nullable = false)
    private Long targetId;

    @Length(max = 200)
    private String content;
    private Boolean process;
    private Long infoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
