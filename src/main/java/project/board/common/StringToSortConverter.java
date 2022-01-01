package project.board.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import project.board.enums.ArticleSortType;

@Component
public class StringToSortConverter implements Converter<String, ArticleSortType> {
	@Override
	public ArticleSortType convert(String source) {
		try {
			return ArticleSortType.valueOf(source.toUpperCase());
		} catch(IllegalArgumentException e) {
			return ArticleSortType.NEWEST;
		}
	}
}
