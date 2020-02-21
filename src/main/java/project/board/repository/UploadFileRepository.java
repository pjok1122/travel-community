package project.board.repository;

import project.board.domain.UploadFile;

public interface UploadFileRepository {

	UploadFile findById(Long id);
	void insert(UploadFile saveFile);

}
