package project.board.entity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.board.enums.Category;
import project.board.enums.Nation;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Article extends BaseTimeEntity{

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;
	
	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 1000000)
	private String content;
	
	private int good;
	private int hit;
	private int commentCount;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Nation nation;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
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
	public Article(Member member, Category category, String title, String content, Nation nation) {
		super();
		this.member = member;
		this.category = category;
		this.title = title;
		this.content = content;
		this.nation = nation;
	}
	

	
	/**
	 * 게시물 수정하기
	 * 
	 * @param title
	 * @param content
	 * @param category
	 * @param nation
	 * @param postFiles
	 */
	public void update(String title, String content, Category category, Nation nation, List<PostFile> postFiles) {
		if(title!=null) this.title = title;
		if(content!=null) this.content = content;
		if(category!=null) this.category = category;
		if(nation!=null) this.nation = nation;
		addPostFiles(postFiles);
	}

	/**
	 * 연관관계 편의 메서드
	 * @param postFile
	 */
	public void addPostFile(PostFile postFile) {
		postFiles.add(postFile);
		postFile.setArticle(this);
	}
	public void addPostFiles(List<PostFile> postFiles) {
		postFiles.forEach(pf-> addPostFile(pf));
	}
	
	
	 
}

