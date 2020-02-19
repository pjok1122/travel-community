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
			
			//파일을 디스크에 저장하고, DB에 메타정보 저장.
			String saveFileName = fileSave(rootLocation.toString(), file);
			UploadFile saveFile = new UploadFile();
			saveFile.setFileName(file.getOriginalFilename());
			saveFile.setSaveFileName(saveFileName);
			saveFile.setContentType(file.getContentType());
			saveFile.setSize(file.getResource().contentLength());
			saveFile.setRegisterDate(LocalDateTime.now());
			saveFile.setFilePath(rootLocation.toString().replace(File.separatorChar, '/') +'/' + saveFileName);   
			uploadFileRepository.save(saveFile); 
			return saveFile;
		} catch(IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	private String fileSave(String rootLocation, MultipartFile file) throws IOException {
		File uploadDir = new File(rootLocation);
		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		//Ajax로 보낸 파일을 디스크로 복사 및 파일 이름 생성.
		UUID uuid = UUID.randomUUID();
		String saveFileName = uuid.toString() + file.getOriginalFilename();
		File saveFile = new File(rootLocation, saveFileName);
		FileCopyUtils.copy(file.getBytes(), saveFile);
		
		return saveFileName;
	}

	public UploadFile load(Long fileId) {
		return uploadFileRepository.findById(fileId);
	}

}
