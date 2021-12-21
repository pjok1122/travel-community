package project.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@MappedSuperclass
@AllArgsConstructor
public abstract class CommonDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;
}
