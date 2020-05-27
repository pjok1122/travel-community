package project.board.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.board.domain.dto.GpsDecimal;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PostFile extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	private String originFileName;
	private String saveFileName;
	private String dirPath;
	private String fileName;
	private String contentType;
	private Long size;
	
	private Double latitude;
	private Double longitude;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "temp_article_id")
	private TempArticle tempArticle;
	
	public PostFile(UploadFile uf) {
		this.originFileName = uf.getOriginFileName();
		this.saveFileName = uf.getSaveFileName();
		this.dirPath = uf.getDirPath();
		this.fileName = uf.getFileName();
		this.contentType = uf.getContentType();
		this.size = uf.getSize();
		this.latitude = uf.getLatitude();
		this.longitude = uf.getLongitude();
	}

	public void setArticle(Article article) {
		this.article = article;
	}
	
	public void seperateDirAndFile(String rootPath, String filePath) {
		this.fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
		this.dirPath = filePath.substring(rootPath.length()+1 , filePath.lastIndexOf('/')+1);
	}
	
	public void setGps(GpsDecimal gpsDecimal) {
		this.latitude = gpsDecimal.getLatitude();
		this.longitude = gpsDecimal.getLongitude();
	}
}
