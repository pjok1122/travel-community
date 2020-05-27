package project.board.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.Report;
import project.board.entity.ReportInfo;
import project.board.entity.dto.ReportRequest;
import project.board.exception.NoExistException;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.ReportInfoRepositoryJpa;
import project.board.repository.ReportRepositoryJpa;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceJpa {
	
	private final ReportRepositoryJpa reportRepository;
	private final MemberRepositoryJpa memberRepository;
	private final ReportInfoRepositoryJpa reportInfoRepository;
	
	@Transactional
	public boolean save(Long memberId, ReportRequest request, Long targetId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException());
		Optional<Report> optReport = reportRepository.findByTargetIdAndTargetAndMember(request.getTargetId(), request.getTarget(), member);
		
		if(optReport.isPresent()) {
			return false;
		} else {
			ReportInfo reportInfo = reportInfoRepository.findById(request.getCheckNo()).orElse(null);
			reportRepository.save(Report.createReport(member, request.getTarget(), targetId, request.getContent(), reportInfo));
			return true;
		}
	}

}
