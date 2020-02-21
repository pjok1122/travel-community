package project.board.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import project.board.domain.UploadFile;
import project.board.repository.UploadFileRepository;
import project.board.util.UploadFileUtils;

@Service
public class UploadFileService {

	@Autowired
	UploadFileRepository uploadFileRepository;
	
	private final Path rootLocation;	//d:/image/
	
	public UploadFileService(String uploadPath) {
		this.rootLocation = Paths.get(uploadPath);
	}
	
	public UploadFile store(MultipartFile file) throws Exception {
		try {
			if(file.isEmpty()) {
				throw new Exception("Failed to store empty file" + file.getOriginalFilename());
			}
			
			// 이미지 파일인지 검사.
			String extension = UploadFileUtils.getExtension(file.getOriginalFilename());
			if(!UploadFileUtils.isImageType(extension)) {
				throw new Exception("Failed to store the file because it is not image file." + file.getOriginalFilename());
			}
			
			//파일을 디스크에 저장하고, DB에 메타정보 저장.
			String filePath = UploadFileUtils.fileSave(rootLocation.toString(), file);
			UploadFile saveFile = new UploadFile();
			saveFile.setFileName(file.getOriginalFilename());
			saveFile.setContentType(extension);
			saveFile.setSize(file.getResource().contentLength());
			saveFile.setFilePath(filePath);
			uploadFileRepository.insert(saveFile); 
			return saveFile;
		} catch(IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}



	public UploadFile load(Long fileId) {
		return uploadFileRepository.findById(fileId);
	}

}
