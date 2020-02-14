package project.board.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import project.board.enums.MyInfo;

@Component
public class StringToMyInfoConverter implements Converter<String, MyInfo>{
	@Override
	public MyInfo convert(String source) {
		try {
			return MyInfo.valueOf(source.toUpperCase());
		} catch(IllegalArgumentException e) {
			return MyInfo.valueOf("MEMBER");
		}
	}
}
