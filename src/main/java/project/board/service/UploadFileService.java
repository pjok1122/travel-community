package project.board.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import lombok.RequiredArgsConstructor;
import project.board.domain.UploadFile;
import project.board.domain.dto.GpsDecimal;
import project.board.repository.UploadFileRepository;
import project.board.util.GpsExtractUtils;
import project.board.util.UploadFileUtils;

@Service
@Transactional(readOnly = true)
public class UploadFileService {

	@Autowired private UploadFileRepository uploadFileRepository;
	@Autowired private GpsExtractUtils gpsUtils;
	
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
			
			//파일을 디스크에 저장
			String filePath = UploadFileUtils.fileSave(rootLocation.toString(), file, email);
			
			//파일의 메타저보를 DB에 저장. (setter 보단 domain에서 처리하는 것이 좋다.)
			UploadFile saveFile = new UploadFile();
			Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
			saveFile.setGps(gpsUtils.toGpsDecimal(metadata));
			saveFile.seperateDirAndFile(rootLocation.toString(),filePath);
			saveFile.setOriginFileName(file.getOriginalFilename());
			saveFile.setContentType(extension);
			saveFile.setSize(file.getResource().contentLength());
			uploadFileRepository.insert(saveFile); 
			return saveFile;
		} catch(IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		} catch(ImageProcessingException e) {
			throw new Exception("Failed to extract metadata of" + file.getOriginalFilename(), e);
		}
	}

	public UploadFile load(String fileName) {
		return uploadFileRepository.selectByFileName(fileName);
	}

}
