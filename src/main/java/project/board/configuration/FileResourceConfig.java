package project.board.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileResourceConfig {

	@Bean(name = "uploadPath")
	public String uploadPath() {
		return "d:/image/";
	}
}
