package project.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.board.controller.ReportRestControllerV2;
import project.board.entity.Member;
import project.board.entity.Report;
import project.board.enums.ReportTargetType;
import project.board.jpa.MemberRepositoryJpa;
import project.board.jpa.ReportRepositoryJpa;

@Service
@RequiredArgsConstructor
public class ReportServiceV2 {

    private final ReportRepositoryJpa reportRepositoryJpa;
    private final MemberRepositoryJpa memberRepositoryJpa;

    private static final String REPORTED_ARTICLE = "이미 신고가 접수된 게시물입니다.";
    private static final String REPORTED_COMMENT = "이미 신고가 접수된 댓글입니다.";
    private static final String SUCCESS_OF_REPORT = "신고가 접수되었습니다.";

    @Transactional
    public String insert(ReportTargetType target, ReportRestControllerV2.ReportRequest request, Long memberId) {

        Member member = memberRepositoryJpa.findById(memberId)
                .orElseThrow(() -> new NullPointerException());

        if (alreadyReported(target, request.getTargetId(), member)) {
            return REPORTED_ARTICLE;
        }

        Report report = Report.builder()
                .target(target)
                .targetId(request.getTargetId())
                .content(request.getContent())
                .infoId(request.getCheckInfo())
                .member(member)
                .build();

        reportRepositoryJpa.save(report);

        return SUCCESS_OF_REPORT;
    }

    private Boolean alreadyReported(ReportTargetType target, Long targetId, Member member) {
        return reportRepositoryJpa.findByTargetAndTargetIdAndMember(target, targetId, member).isPresent();
    }

}
