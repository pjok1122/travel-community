package project.board.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import project.board.entity.PostFile;
import project.board.entity.UploadFile;
import project.board.jpa.UploadFileRepositoryJpa;
import project.board.repository.UploadFileRepository;
import project.board.util.GpsExtractUtils;
import project.board.util.UploadFileUtils;

@Service
@Transactional(readOnly = true)
public class UploadFileService {

	@Autowired private UploadFileRepositoryJpa uploadFileRepository;
	@Autowired private GpsExtractUtils gpsUtils;

	private final Path rootLocation;

	@Value("${image.temp-storage.uri}")
	private String tempStorageUri;

	@Value("${image.storage.uri}")
	private String storageUri;

	@Value("${image.temp-storage.path}")
	private String tempStoragePath;

	@Value("${image.storage.date-path-format}")
	private String datePathFormat;


	public UploadFileService(@Value("${image.temp-storage.path}") String uploadPath) {
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
			uploadFileRepository.save(saveFile);
			return saveFile;
		} catch(IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		} catch(ImageProcessingException e) {
			throw new Exception("Failed to extract metadata of" + file.getOriginalFilename(), e);
		}
	}

	public List<UploadFile> findByFileNameIn(List<String> imageUrls) {
		// /upload/image/2022/01/01/imageName --> imageName
		Set<String> imageFileNames = new HashSet<>();

		Set<String> tempImageNames =
				imageUrls.stream()
						 .filter(name -> name.contains(tempStorageUri))
						 .filter(name -> name.length() > tempStorageUri.length() + datePathFormat.length())
						 .map(name -> name.substring(tempStorageUri.length() + datePathFormat.length()))
						 .collect(Collectors.toSet());

		Set<String> postImageNames =
				imageUrls.stream()
						 .filter(name -> name.contains(storageUri))
						 .filter(name -> name.length() > storageUri.length() + datePathFormat
								 .length())
						 .map(name -> name.substring(storageUri.length() + datePathFormat.length()))
						 .collect(Collectors.toSet());

		imageFileNames.addAll(tempImageNames);
		imageFileNames.addAll(postImageNames);

		return uploadFileRepository.findByFileNameIn(imageFileNames);
	}

	public List<PostFile> createPostFileByFileNameIn(List<String> imageUrls) {
        List<UploadFile> tempImages = findByFileNameIn(imageUrls);
        return tempImages.stream()
                         .map(PostFile::create)
                         .collect(Collectors.toList());
	}

}
