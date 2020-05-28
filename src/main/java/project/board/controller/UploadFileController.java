//package project.board.controller;
//
//import java.util.concurrent.TimeUnit;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.http.CacheControl;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import lombok.RequiredArgsConstructor;
//import project.board.domain.UploadFile;
//import project.board.repository.PostFileRepositoryJpa;
//import project.board.service.UploadFileService;
//
//@Controller
//@RequiredArgsConstructor
//public class UploadFileController {
//	
//	private final UploadFileService imageService;
//	private final ResourceLoader resourceLoader;
//	
//	@Value("${image.baseUrl}")
//	private String imageBaseUrl;
//	
//	@Value("${image.dir.path.format}")
//	private String dirPathFormat;
//	
//	@Value("${image.save.path}")
//	private String imageSavePath;
//	
//	@Value("${image.post.path}")
//	private String imagePostPath;
//	
//	@PostMapping("/image")
//	public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file, @RequestParam("email") String email){
//		try {
//			UploadFile uploadFile = imageService.store(file, email);
//			return ResponseEntity.ok().body(imageBaseUrl + uploadFile.getDirPath().replace('/', '.') + uploadFile.getFileName());
//		} catch(Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.badRequest().build();
//		}
//	}
//	
//	@GetMapping(value={"/upload/image/{filePath}", "/postfile/image/{filePath}"})
//	public ResponseEntity<?> getFile(
//			@PathVariable("filePath") String filePath,
//			HttpServletRequest request){
//		try {
//			String rootPath = imagePostPath;
//			if(request.getRequestURI().startsWith("/upload")) {
//				rootPath = imageSavePath;
//			}
//			String dirPath = filePath.substring(0,dirPathFormat.length()).replace('.', '/');
//			String fileName = filePath.substring(dirPathFormat.length());
//			Resource resource = resourceLoader.getResource("file:" + rootPath + dirPath + fileName);
//			return ResponseEntity.ok()
//					.cacheControl(CacheControl.maxAge(604800L, TimeUnit.SECONDS))
//					.body(resource);
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.badRequest().build();
//		}
//	}
//}
