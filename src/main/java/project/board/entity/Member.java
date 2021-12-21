package project.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.Builder.Default;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(name="EMAIL_UNIQUE", columnNames = {"email"}) })
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    @Column(nullable = false)
    private LocalDateTime registerDate;

    private LocalDateTime updateDate;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    private LocalDateTime loginDate;

    @Default
    @Column(columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'USER'")
    private String role = "USER";

}
