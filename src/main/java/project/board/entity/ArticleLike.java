package project.board.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Entity
@EqualsAndHashCode(of= {"article", "member"}, callSuper = false)
@Table(uniqueConstraints = {@UniqueConstraint(name="ARTICLE_MEMBER_UNIQUE", columnNames = {"article_id", "member_id"}) })
public class ArticleLike extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	// ======== 생성 메서드 ===========
	public static ArticleLike createArticleLike(Article article, Member member) {
		ArticleLike articleLike = new ArticleLike();
		articleLike.article = article;
		articleLike.member = member;
		return articleLike;
	}
	
}
