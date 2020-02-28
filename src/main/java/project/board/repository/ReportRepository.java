package project.board.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportRepository {

	void insertReport(String target, Long targetId, Long memberId, Long infoId, String content);

}
