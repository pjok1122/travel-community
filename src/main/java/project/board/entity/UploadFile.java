package project.board.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import project.board.domain.dto.GpsDecimal;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
public class UploadFile extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	private String originFileName;
	private String dirPath;
	private String fileName;
	private String contentType;
	private Long size;
	
	private Double latitude;
	private Double longitude;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "temp_article_id")
	private TempArticle tempArticle;
	
	public void seperateDirAndFile(String rootPath, String savePath) {
		this.fileName = savePath.substring(savePath.lastIndexOf('/')+1, savePath.length());
		this.dirPath = savePath.substring(rootPath.length(), savePath.lastIndexOf('/')+1);
	}
	
	public void setGps(GpsDecimal gpsDecimal) {
		this.latitude = gpsDecimal.getLatitude();
		this.longitude = gpsDecimal.getLongitude();
	}

	// ======== 생성 메서드 =========
	public static UploadFile createUploadFile(
			String originFileName, Long size, String contentType,
			String rootPath, String savePath,
			GpsDecimal gpsDecimal)
	{
		UploadFile file = new UploadFile();
		file.contentType = contentType;
		file.size = size;
		file.originFileName = originFileName;
		file.seperateDirAndFile(rootPath, savePath);
		file.setGps(gpsDecimal);
		return file;
	}
	
	// ============ 비즈니스 로직 =========
	public void add(TempArticle article) {
		this.tempArticle = article;
	}
}
