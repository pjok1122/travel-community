package project.board.domain;

import java.io.File;
import java.io.IOException;

import org.apache.ibatis.type.Alias;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import project.board.domain.dto.GpsDecimal;
import project.board.util.GpsExtractUtils;

@Getter @Setter
@Alias("uploadFile")
@ToString
public class UploadFile extends CommonDomain{
//	private Long id;
	private String originFileName;
	private String saveFileName;
	private String dirPath;
	private String fileName;
	private String contentType;
	private Long size;
//	private LocalDateTime registerDate;
//	private LocalDateTime updateDate;
	
	private Double latitude;
	private Double longitude;
	
	//DB에 없는 값.
	private Long articleId;
	
	public void seperateDirAndFile(String rootPath, String filePath) {
		this.fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
		this.dirPath = filePath.substring(rootPath.length()+1 , filePath.lastIndexOf('/')+1);
	}
	
	public void setGps(GpsDecimal gpsDecimal) {
		this.latitude = gpsDecimal.getLatitude();
		this.longitude = gpsDecimal.getLongitude();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((articleId == null) ? 0 : articleId.hashCode());
		result = prime * result + ((dirPath == null) ? 0 : dirPath.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadFile other = (UploadFile) obj;
		if (articleId == null) {
			if (other.articleId != null)
				return false;
		} else if (!articleId.equals(other.articleId))
			return false;
		if (dirPath == null) {
			if (other.dirPath != null)
				return false;
		} else if (!dirPath.equals(other.dirPath))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	
}
