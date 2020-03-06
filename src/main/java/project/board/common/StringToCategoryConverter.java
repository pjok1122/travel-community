package project.board.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import project.board.enums.Category;

@Component
public class StringToCategoryConverter implements Converter<String, Category>{
	@Override
	public Category convert(String source) {
		return Category.valueOf(source.toUpperCase());
	}
}
