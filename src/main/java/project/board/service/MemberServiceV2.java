package project.board.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.jpa.MemberRepositoryJpa;
import project.board.util.Sha256Utils;

@RequiredArgsConstructor
@Service
public class MemberServiceV2 {
    private final MemberRepositoryJpa memberRepository;
    private final Sha256Utils sha256Utils;

    @Transactional
    public project.board.entity.Member login(String email, String password) {
        project.board.entity.Member member = memberRepository.findByEmail(email).orElse(null);
        if (member != null && passwordCompare(password, member)) {
            member.login();
        }

        return member;
    }

    public boolean passwordCompare(String password, Member savedMember) {
        String hashPassword = sha256Utils.sha256(password, savedMember.getSalt());
        return StringUtils.equals(hashPassword, savedMember.getPassword());
    }

    @Transactional
    public Member save(String email, String password) {
        String salt = UUID.randomUUID().toString();

        String hashPassword = sha256Utils.sha256(password, salt);
        Member member = Member.create(email, salt, hashPassword);
        return memberRepository.save(member);
    }

    @Transactional
    public void update(String email, String password) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException());

        String salt = UUID.randomUUID().toString();
        String hashPassword = sha256Utils.sha256(password, salt);

        member.update(hashPassword, salt);
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
