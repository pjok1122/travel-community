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
	public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file){
		try {
			UploadFile uploadFile = imageService.store(file);
			return ResponseEntity.ok().body("/image/" + uploadFile.getId());
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/image/{fileId}")
	public ResponseEntity<?> getFile(@PathVariable Long fileId){
		try {
			UploadFile uploadFile = imageService.load(fileId);
			Resource resource = resourceLoader.getResource("file:" + uploadFile.getFilePath());
			return ResponseEntity.ok().body(resource);
			
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}
