package project.board.repository;

import project.board.domain.UploadFile;

public interface UploadFileRepository {

	void save(UploadFile saveFile);
	UploadFile findById(Long id);

}
