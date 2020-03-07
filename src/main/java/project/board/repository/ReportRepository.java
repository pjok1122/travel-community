package project.board.repository;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Report;

@Mapper
public interface ReportRepository {

	void insertReport(String target, Long targetId, Long memberId, Long infoId, String content);

	Report selectReportByTargetIdAndMemberId(String target, Long targetId, Long memberId);

}
