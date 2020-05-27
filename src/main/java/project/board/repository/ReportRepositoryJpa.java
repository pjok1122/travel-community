package project.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Member;
import project.board.entity.Report;
import project.board.enums.ReportTarget;

public interface ReportRepositoryJpa extends JpaRepository<Report, Long>{

	Optional<Report> findByTargetIdAndTargetAndMember(Long targetId, ReportTarget target, Member member);

}
