package project.board.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.board.jpa.MemberRepositoryJpa;

@RequiredArgsConstructor
@Service
public class MemberServiceV2 {
    private final MemberRepositoryJpa memberRepositoryJpa;
}
