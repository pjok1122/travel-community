package project.board.repository;

import java.util.List;

import project.board.domain.UploadFile;

public interface UploadFileRepository {

	UploadFile selectByFileName(String fileName);
	void insert(UploadFile saveFile);
	List<UploadFile> selectByFileNames(String[] fileNames);

}
