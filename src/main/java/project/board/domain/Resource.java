package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("resource")
public class Resource {
	private Long id;
	private String fileName;
	private String filePath;
	private String contentType;
	private Long size;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;	
}
