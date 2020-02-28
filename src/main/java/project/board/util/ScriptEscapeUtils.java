package project.board.util;

import org.springframework.stereotype.Component;

@Component
public class ScriptEscapeUtils {
	
	public String scriptEscpae(String content) {
		return content.replaceAll("<", "&lt;");
	}
}
