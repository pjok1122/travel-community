package project.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.jpa.MemberRepositoryJpa;

@RequiredArgsConstructor
@Service
public class MemberServiceV2 {
    private final MemberRepositoryJpa memberRepository;

    @Transactional
    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member != null && member.verifyPassword(password)) {
            member.login();
        }

        return member;
    }

    @Transactional
    public Member save(String email, String password) {
        Member member = Member.create(email, password);
        return memberRepository.save(member);
    }

    @Transactional
    public void update(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException());
        member.update(password);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        memberRepository.delete(member);
    }
}
