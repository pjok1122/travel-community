package project.board.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import lombok.RequiredArgsConstructor;
import project.board.entity.PostFile;
import project.board.entity.UploadFile;
import project.board.repository.UploadFileRepositoryJpa;
import project.board.util.GpsExtractUtils;
import project.board.util.UploadFileUtils;

@Service
@RequiredArgsConstructor
public class ImageServiceJpa {
	
	private final UploadFileRepositoryJpa uploadFileRepository;
	private final GpsExtractUtils gpsUtils;
	@Value("${image.baseUrl}")
	private String imageBaseUrl;
	
	@Value("${image.dir.path.format}")
	private String dirPathFormat;
	
	@Value("${image.save.path}")
	private String uploadPath;
	
	
	/**
	 * ArticleForm의 imageName를 List<PostFile>로 변환해 제공한다.
	 * @param imageNames
	 * @return List<PostFile>
	 */
	protected List<PostFile> strToPostFiles(String imageName) {
		List<UploadFile> uploadFiles = strToUploadFiles(imageName);
		return uploadFiles.stream().map(uf-> new PostFile(uf))
			.collect(Collectors.toList());
	}
	
	/**
	 * ArticleForm의 imageName을 가공해, List<UploadFile>를 조회한다.
	 * @param imageName
	 * @return
	 */
	protected List<UploadFile> strToUploadFiles(String imageName){
		if(imageName == null) return new ArrayList<UploadFile>();
		
		List<String> fileNames = Arrays.asList(imageName.trim().split(" "));
		
		// 이미지 이름을 List에 저장한다.
		fileNames = fileNames.stream().filter(name-> name.length() > imageBaseUrl.length()+dirPathFormat.length())
						.map(name -> name.substring(imageBaseUrl.length()+dirPathFormat.length()))
						.collect(Collectors.toList());
		
		if(fileNames.isEmpty()) return new ArrayList<UploadFile>();
		
		//임시저장소에서 불러온다.
		return uploadFileRepository.findByFileNames(fileNames);
		
	}


	/**
	 * 파일을 디스크에 저장한다.
	 * @param file 이미지 파일
	 * @param email 파일 주인의 이메일
	 * @return Server의 디스크에 저장된 파일의 경로를 반환한다.
	 * @throws Exception
	 */
	public String diskSave(MultipartFile file, String email) throws Exception {
			if(file.isEmpty()) throw new Exception("Failed to store empty file" + file.getOriginalFilename());
			String extension = UploadFileUtils.getExtension(file.getOriginalFilename());
			if(!UploadFileUtils.isImageType(extension)) throw new Exception("Failed to store the file becuase it isn't a image file");
			
			//파일을 디스크에 저장한다.
			return UploadFileUtils.fileSave(uploadPath, file, email);
	}

	/**
	 * 이미지 파일에 대한 메타정보를 UploadFile 테이블에 저장한다.
	 * @param file 이미지 파일
	 * @param savePath 파일 저장 경로
	 * @return 저장된 파일을 식별하는 URL 주소
	 * @throws ImageProcessingException
	 * @throws IOException
	 */
	@Transactional
	public String save(MultipartFile file, String savePath) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
		UploadFile uploadFile = UploadFile.createUploadFile(file.getOriginalFilename(), file.getSize(), file.getContentType(),
				uploadPath, savePath, gpsUtils.toGpsDecimal(metadata));
		uploadFileRepository.save(uploadFile);
		
		return imageBaseUrl + uploadFile.getDirPath().replace('/', '.') + uploadFile.getFileName();
	}

}
