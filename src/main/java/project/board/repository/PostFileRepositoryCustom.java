package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.entity.PostFile;

@Mapper
public interface PostFileRepositoryCustom {
	
	public List<PostFile> selectByArticleIds(List<Long> articleIds);

}
