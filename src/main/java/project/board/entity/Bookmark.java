package project.board.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;

@Getter
@Entity
public class Bookmark extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	private Article article;

	// ========== 생성 메서드 =========
	public static Bookmark createBookmark(Member member, Article article) {
		Bookmark bookmark = new Bookmark();
		bookmark.member = member;
		bookmark.article = article;
		return bookmark;
	}
}
