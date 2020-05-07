package project.board.entity;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.board.enums.Nation;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Article extends BaseTimeEntity{

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	private String title;
	
	private String content;
	
	private int good;
	private int hit;
	
	@Enumerated(EnumType.STRING)
	private Nation nation;

	@OneToMany(mappedBy = "article")
	private List<PostFile> postFiles = new ArrayList<>();
	
	public void hitUp() {
		hit++;
	}
	public void goodUp() {
		good++;
	}
	public void goodDown() {
		good--;
	}
	
	 
}

