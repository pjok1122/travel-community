package project.board.repository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import project.board.entity.PostFile;

@RequiredArgsConstructor
public class PostFileRepositoryCustomImpl implements PostFileRepositoryCustom{
	
	private final PostFileRepositoryCustom postFileRepositoryCustom;
	
	@Override
	public List<PostFile> selectByArticleIds(List<Long> articleIds) {
		return postFileRepositoryCustom.selectByArticleIds(articleIds);
	}

}
