package project.board.common;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import project.board.util.UploadFileUtils;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UploadFileUtilsTest {
	
	@Autowired UploadFileUtils utils;
	
	@Test
	public void 시간_TO_16진수() throws UnsupportedEncodingException {
		LocalDateTime time = LocalDateTime.now();
		String timeString = extractTime(time.toString());
		String hex = toHex(timeString);
//		System.out.println(hex);
	}

	private String extractTime(String timeString) {
		return timeString.substring(timeString.indexOf('T')+1, timeString.length());
	}
	
	public String toHex(String arg) throws UnsupportedEncodingException {
	    return String.format("%x", new BigInteger(1, arg.getBytes("UTF-8")));
	}
	
}
