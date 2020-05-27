package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.ReportInfo;

public interface ReportInfoRepositoryJpa extends JpaRepository<ReportInfo, Long>{

}
