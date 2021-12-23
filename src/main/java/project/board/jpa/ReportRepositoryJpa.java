package project.board.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.Member;
import project.board.entity.Report;
import project.board.enums.ReportTargetType;

import java.util.Optional;

public interface ReportRepositoryJpa extends JpaRepository<Report, Long> {

    Optional<Report> findByTargetAndTargetIdAndMember(ReportTargetType target, Long targetId, Member member);
}
