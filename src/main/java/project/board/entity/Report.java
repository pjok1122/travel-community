package project.board.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import project.board.enums.ReportTargetType;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class Report extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportTargetType target;

    @Column(nullable = false)
    private Long targetId;

    @Length(max = 200)
    private String content;
    private Boolean process = false;
    private Long infoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public Report(ReportTargetType target, Long targetId, String content, Long infoId, Member member) {
        this.target = target;
        this.targetId = targetId;
        this.content = content;
        this.infoId = infoId;
        this.member = member;
    }
}
