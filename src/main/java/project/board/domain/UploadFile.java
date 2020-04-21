package project.board.domain;

import java.io.File;

import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("uploadFile")
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
	
	public void seperateDirAndFile(String filePath) {
		this.fileName = filePath.substring(filePath.lastIndexOf('/')+1, filePath.length());
		this.dirPath = filePath.substring(0, filePath.lastIndexOf('/')+1);
	}
	
}
