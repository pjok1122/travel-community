package project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.board.entity.UploadFile;

public interface UploadFileRepositoryJpa extends JpaRepository<UploadFile, Long>{

	@Query("select uf from UploadFile uf where uf.fileName in :fileNames order by uf.createdDate")
	List<UploadFile> findByFileNames(List<String> fileNames);

}
