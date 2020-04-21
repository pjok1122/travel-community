package project.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import project.board.domain.UploadFile;
import project.board.service.UploadFileService;

@Controller
public class UploadFileController {
	
	@Autowired
	UploadFileService imageService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@PostMapping("/image")
	public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file, @RequestParam("email") String email){
		try {
			UploadFile uploadFile = imageService.store(file, email);
			return ResponseEntity.ok().body("/upload/image/" + uploadFile.getFileName());
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/upload/image/{filePath}")
	public ResponseEntity<?> getFile(@PathVariable String filePath){
		try {
			UploadFile uploadFile = imageService.load(filePath);
			Resource resource = resourceLoader.getResource("file:" + uploadFile.getDirPath() + uploadFile.getFileName());
			return ResponseEntity.ok().body(resource);
			
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}
