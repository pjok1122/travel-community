package project.board.entity;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Getter
@Entity
public class Category extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 32)
    private String title;

    @Column(name = "`order`", columnDefinition = "INT NULL DEFAULT 0")
    private Integer order = 0;
}
