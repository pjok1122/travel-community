package project.board.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import project.board.domain.UploadFile;
import project.board.enums.ImageMediaType;


@Component
@Slf4j
public class UploadFileUtils {
	
	@Value("{image.save.path}")
	private String imageSavePath; 
	
	/**
	 * 파일명의 확장자를 토대로 이미지 파일인지 확인하는 메서드
	 */
	public static Boolean isImageType(String mediaType) {
		try {
			log.info(mediaType.toUpperCase());
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
	 * @param String email
	 * 			업로드한 사람
	 * @return 파일이 저장된 최종 경로
	 * @throws IOException
	 */
	public static String fileSave(String rootLocation, MultipartFile file, String email) throws IOException {
		File uploadDir = new File(rootLocation);
		File saveFile = null;
		
		if(!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		while(true) {
			String uuid = email.substring(0, email.indexOf('@')) + currentTimeToHex() + UUID.randomUUID().toString().replace("-", "");
			String saveFileName = uuid + "." + getExtension(file.getOriginalFilename());
			
			//폴더경로 생성
			String dirPath = makeSavePath(rootLocation);
			
			saveFile = new File(rootLocation + dirPath, saveFileName);
			if(saveFile.exists()) {
				continue;
			}
			else {
				FileCopyUtils.copy(file.getBytes(), saveFile);
				break;
			}
		}
		log.info(saveFile.getPath());
		return saveFile.getPath().replace(File.separatorChar, '/');
	}

	public static void tempFileCopyAsPostFile(String imageSavePath, String imagePostPath, List<UploadFile> postFiles) {
		postFiles.forEach(pf->{
			File postDir =new File(imagePostPath + pf.getDirPath());
			if(!postDir.exists()) {
				postDir.mkdirs();
			}
			File in = new File(imageSavePath + pf.getDirPath() + pf.getFileName());
			File out = new File(postDir, pf.getFileName());
			try {
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
	}
	
	private static String makeSavePath(String rootLocation) {
		Calendar cal = Calendar.getInstance();
		
		String yearPath = File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1);
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		File dirPath = new File(rootLocation + datePath);
		
		if(!dirPath.exists()) {
			dirPath.mkdirs();
		}
		return datePath;
	}

//	private static void makeDir(String rootLocation, String... paths) {
//		if(new File(paths[paths.length-1]).exists()) {
//			return;
//		}
//		
//		for (String path : paths) {
//			File dirPath = new File(rootLocation + path);
//			
//			if(!dirPath.exists()) {
//				dirPath.mkdir();
//			}
//		}
//	}
	
	private static String currentTimeToHex() throws UnsupportedEncodingException {
		LocalDateTime now = LocalDateTime.now();
		return toHex(localDateTimeToHMS(now.toString()));
	}


	private static String localDateTimeToHMS(String str) {
		return str.substring(str.indexOf('T')+1, str.lastIndexOf('.'));
	}


	private static String toHex(String arg) throws UnsupportedEncodingException {
	    return String.format("%x", new BigInteger(1, arg.getBytes("UTF-8")));
	}
}
