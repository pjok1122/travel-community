package project.board.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import project.board.enums.Sort;

@Component
public class StringToSortConverter implements Converter<String, Sort> {
	@Override
	public Sort convert(String source) {
		try {
			return Sort.valueOf(source.toUpperCase());
		} catch(IllegalArgumentException e) {
			return Sort.NEWEST;
		}
	}
}
