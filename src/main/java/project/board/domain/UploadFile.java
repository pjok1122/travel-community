package project.board.domain;

import org.apache.ibatis.type.Alias;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
	
	//DB에 없는 값.
	private Long articleId;
	
	public void seperateDirAndFile(String rootPath, String filePath) {
		this.fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
		this.dirPath = filePath.substring(rootPath.length()+1 , filePath.lastIndexOf('/')+1);
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
