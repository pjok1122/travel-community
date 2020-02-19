package project.board.domain;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("uploadFile")
public class UploadFile extends CommonDomain{
//	private Long id;
	private String fileName;
	private String saveFileName;
	private String filePath;
	private String contentType;
	private Long size;
//	private LocalDateTime registerDate;
//	private LocalDateTime updateDate;	
}
