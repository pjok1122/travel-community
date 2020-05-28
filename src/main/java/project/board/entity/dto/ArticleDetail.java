package project.board.entity.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.board.domain.dto.GpsDecimal;
import project.board.entity.Article;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetail {
	private Long articleId;
	private String writer;
	private Category category;
	private Nation nation;
	private String title;
	private String content;
	private int hit;
	private int good;
	private int commentCount;
	private LocalDateTime createdDate;
	private LocalDateTime updateDate;
	
	private List<GpsDecimal> gpsInfo;
	
	private boolean liked;
	private boolean bookmarked;
	
	public ArticleDetail(Article article, boolean liked, boolean bookmarked) {
		articleId = article.getId();
		writer = article.getMember().getEmail();
		category = article.getCategory();
		nation = article.getNation();
		title = article.getTitle();
		content = article.getContent();
		hit = article.getHit();
		good = article.getGood();
		commentCount = article.getCommentCount();
		createdDate = article.getCreatedDate();
		updateDate = article.getLastModifiedDate();
		this.liked = liked;
		this.bookmarked = bookmarked;
		
		gpsInfo = article.getPostFiles().stream()
					.map(o-> new GpsDecimal(o.getLatitude(), o.getLongitude()))
					.collect(Collectors.toList());
	}
}
