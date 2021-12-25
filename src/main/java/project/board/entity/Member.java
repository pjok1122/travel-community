package project.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.*;
import lombok.Builder.Default;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(name="EMAIL_UNIQUE", columnNames = {"email"}) })
public class Member extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public static Member create(String email, String salt, String password) {
        return Member.builder()
                     .email(email)
                     .salt(salt)
                     .password(password)
                     .loginDate(LocalDateTime.now())
                     .role("USER")
                     .build();
    }

    public void login() {
        loginDate = LocalDateTime.now();
    }

    public void update(String password, String salt) {
        this.password = password;
        this.salt = salt;
    }

}
