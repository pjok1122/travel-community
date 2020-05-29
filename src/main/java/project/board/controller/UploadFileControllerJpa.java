package project.board.controller;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.service.ImageServiceJpa;
import project.board.util.MySessionUtils;

@RestController
@RequiredArgsConstructor
public class UploadFileControllerJpa {
	private final ImageServiceJpa imageService;
	private final ResourceLoader resourceLoader;
	private final MySessionUtils sUtils;
	
	@Value("${image.baseUrl}")
	private String imageBaseUrl;
	
	@Value("${image.dir.path.format}")
	private String dirPathFormat;
	
	@Value("${image.save.path}")
	private String imageSavePath;
	
	@Value("${image.post.path}")
	private String imagePostPath;
	
	
	/**
	 * 이미지 업로드를 처리한다.
	 * @param file 이미지 파일
	 * @param session 세션 정보
	 * @return 상태코드 200. 저장된 이미지를 식별할 수 있는 URL을 반환한다.
	 * @throws Exception
	 */
	@PostMapping("/ajax/v1/image")
	@AjaxLoginAuth
	public ResponseEntity<?> imageUploadProcess(
			@RequestParam("file") MultipartFile file,
			HttpSession session) throws Exception
	{
		//파일을 디스크에 저장한다.
		String savePath = imageService.diskSave(file, sUtils.getMemberEmail(session));
		
		//파일의 메타정보를 DB에 저장한다.
		String url = imageService.save(file, savePath);
		return ResponseEntity.ok(url);
	}
	
	@GetMapping(value= {"/upload/image/{url}", "postfile/image/{url}"})
	public ResponseEntity<?> getImageFile(
			@PathVariable("url") String url,
			HttpServletRequest request
			)
	{
		String dirPath = url.substring(0, dirPathFormat.length()).replace('.', '/');
		String fileName = url.substring(dirPathFormat.length());
		Resource resource = resourceLoader.getResource("file:" + imagePostPath + dirPath + fileName);
		if(!resource.isReadable()) {
			resource = resourceLoader.getResource("file:" + imageSavePath + dirPath + fileName);
		}
		
		if(resource.isReadable()) {
			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(604800L, TimeUnit.SECONDS))
					.body(resource);
		} else {
			return ResponseEntity.status(404).build();
		}
	}
}
