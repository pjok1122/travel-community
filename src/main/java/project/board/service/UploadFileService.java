package project.board.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import project.board.domain.UploadFile;
import project.board.repository.UploadFileRepository;
import project.board.util.UploadFileUtils;

@Service
@Transactional(readOnly = true)
public class UploadFileService {

	@Autowired
	UploadFileRepository uploadFileRepository;
	
	private final Path rootLocation;	
	
	
	public UploadFileService(@Value("${image.save.path}") String uploadPath) {
		this.rootLocation = Paths.get(uploadPath);
	}
	
	@Transactional
	public UploadFile store(MultipartFile file, String email) throws Exception {
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
			String filePath = UploadFileUtils.fileSave(rootLocation.toString(), file, email);
			UploadFile saveFile = new UploadFile();
			saveFile.seperateDirAndFile(rootLocation.toString(),filePath);
			saveFile.setOriginFileName(file.getOriginalFilename());
			saveFile.setContentType(extension);
			saveFile.setSize(file.getResource().contentLength());
			uploadFileRepository.insert(saveFile); 
			return saveFile;
		} catch(IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	public UploadFile load(String fileName) {
		return uploadFileRepository.selectByFileName(fileName);
	}

}
