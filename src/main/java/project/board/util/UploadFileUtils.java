package project.board.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import project.board.enums.ImageMediaType;


@Component
public class UploadFileUtils {
		
	public static Boolean isImageType(String mediaType) {
		try {
			ImageMediaType.valueOf(mediaType.toUpperCase());
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
	
	
	/**
	 * 파일 이름으로부터 확장자를 추출하여 반환.
	 * 
	 * @param fileName
	 * @return "파일 이름의 맨 뒤 확장자"
	 */
	public static String getExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		
		if(dotIndex != -1 && dotIndex < fileName.length()-1) {
			return fileName.substring(dotIndex+1);
		} else {
			return "";
		}
	}
	/**
	 * 업로드된 파일을 디스크에 저장하고 파일이 저장된 위치를 반환.
	 * 
	 * @param rootLocation
	 * 			파일이 저장되는 루트 디렉터리
	 * @param multipartFile
	 * 			업로드한 파일
	 * @return 파일이 저장된 최종 경로
	 * @throws IOException
	 */
	public static String fileSave(String rootLocation, MultipartFile file) throws IOException {
		File uploadDir = new File(rootLocation);
		File saveFile = null;
		
		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		while(true) {
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replace("-", "");
			String saveFileName = uuid + "." + getExtension(file.getOriginalFilename());
			
			String savePath = makeSavePath(rootLocation);
			
			saveFile = new File(rootLocation + savePath, saveFileName);
			if(saveFile.exists()) {
				continue;
			}
			else {
				FileCopyUtils.copy(file.getBytes(), saveFile);
				break;
			}
		}
		return saveFile.getPath().replace(File.separatorChar, '/');
	}

	private static String makeSavePath(String rootLocation) {
		Calendar cal = Calendar.getInstance();
		
		String yearPath = File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH));
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		makeDir(rootLocation, yearPath, monthPath, datePath);
		
		return datePath;
	}

	private static void makeDir(String rootLocation, String... paths) {
		if(new File(paths[paths.length-1]).exists()) {
			return;
		}
		
		for (String path : paths) {
			File dirPath = new File(rootLocation + path);
			
			if(!dirPath.exists()) {
				dirPath.mkdir();
			}
		}
	}
}
