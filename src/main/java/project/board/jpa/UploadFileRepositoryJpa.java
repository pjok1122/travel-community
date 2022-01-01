package project.board.jpa;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.UploadFile;

public interface UploadFileRepositoryJpa extends JpaRepository<UploadFile, Long> {
    List<UploadFile> findByFileNameIn(Collection<String> fileNames);
}
