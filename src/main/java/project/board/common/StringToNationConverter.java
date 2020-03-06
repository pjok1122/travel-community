package project.board.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import project.board.enums.Nation;

@Component
public class StringToNationConverter implements Converter<String, Nation>{
	
	@Override
	public Nation convert(String source) {
		try {
			return Nation.valueOf(source.toUpperCase());
		} catch(IllegalArgumentException e) {
			return Nation.ALL;
		}
	}

}
