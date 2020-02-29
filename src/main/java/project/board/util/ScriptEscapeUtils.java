package project.board.util;

import org.springframework.stereotype.Component;

@Component
public class ScriptEscapeUtils {
	
	public String scriptEscape(String content) {
		return content.replaceAll("<", "&lt;");
	}
}
