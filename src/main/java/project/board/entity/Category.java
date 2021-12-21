package project.board.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class Category extends CommonDomain {

    @Length(max = 32)
    private String title;

    @Column(name = "`order`", columnDefinition = "INT NULL DEFAULT 0")
    private Integer order = 0;
}
