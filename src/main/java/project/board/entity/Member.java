package project.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import lombok.*;
import lombok.Builder.Default;
import project.board.util.Sha256Utils;

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

    // === Cascade 삭제를 위한 매핑 정보 ===
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();

    public static Member create(String email, String password) {
        String salt = UUID.randomUUID().toString();
        return Member.builder()
                     .email(email)
                     .salt(salt)
                     .password(Sha256Utils.hash(password, salt))
                     .loginDate(LocalDateTime.now())
                     .role("USER")
                     .build();
    }

    public void login() {
        loginDate = LocalDateTime.now();
    }

    public void update(String originPassword) {
        String salt = UUID.randomUUID().toString();
        this.password = Sha256Utils.hash(originPassword, salt);
        this.salt = salt;
    }

    public boolean verifyPassword(String originPassword) {
        String hashPassword = Sha256Utils.hash(originPassword, salt);
        return StringUtils.equals(hashPassword, password);
    }

}
