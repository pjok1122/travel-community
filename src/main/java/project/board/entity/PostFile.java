package project.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class PostFile extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originFileName;

    @Column(nullable = false)
    private String dirPath;

    @Column(nullable = false)
    private String fileName;

    @Length(max = 6)
    private String contentType;
    private Long size;

    private Double latitude;
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    private Article article;

    public static PostFile create(UploadFile uf) {
        PostFile postFile = new PostFile();
        postFile.originFileName = uf.getOriginFileName();
        postFile.dirPath = uf.getDirPath();
        postFile.fileName = uf.getFileName();
        postFile.contentType = uf.getContentType();
        postFile.size = uf.getSize();
        postFile.latitude = uf.getLatitude();
        postFile.longitude = uf.getLongitude();
        return postFile;
    }
}
