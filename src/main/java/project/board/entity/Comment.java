package project.board.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;

@Entity
@Getter
public class Comment extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_comment_id")
	private Comment parent;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	private List<Comment> children = new ArrayList<>();
	private int good;
	
}

