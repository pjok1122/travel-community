package project.board.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import project.board.domain.UploadFile;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.GpsDecimal;

@Repository
public interface PostFileRepository {
	
	public void insertPostFiles(Collection<UploadFile> uploadFiles);

	public List<UploadFile> selectByArticleIds(List<Long> popularArticleIds);
	
	public UploadFile selectByFileName(String fileName);

	public List<GpsDecimal> selectByArticleId(Long articleId);
}
