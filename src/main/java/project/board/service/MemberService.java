package project.board.service;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.common.Sha256Utils;
import project.board.domain.Member;
import project.board.dto.MemberDto;
import project.board.repository.MemberRepository;

@Service
public class MemberService {

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	Sha256Utils sha256Utils;
	
	public Member login(MemberDto memberDto) {
		Member savedMember = memberRepository.findByEmail(memberDto.getEmail());
		if (savedMember == null)
			return null;
		if (passwordCompare(memberDto, savedMember)) {
			//로그인 시간 기록
			savedMember.setLoginDate(LocalDateTime.now());
			memberRepository.update(savedMember);
			return savedMember;
		}
		
		return null;
	}
	
	public Boolean passwordCompare(MemberDto memberDto, Member savedMember) {
		String salt = savedMember.getSalt();
		String pwd = memberDto.getPassword();
		String hashPwd = sha256Utils.sha256(pwd, salt);
		
		if (hashPwd.equals(savedMember.getPassword())){
			return true;
		}
		
		return false;
	}

	public Member save(MemberDto memberDto) {
		Member member = Member.builder().email(memberDto.getEmail())
						.password(memberDto.getPassword())
						.salt(UUID.randomUUID().toString()).build();
		
		Long memberId = memberRepository.insert(member);
//		memberRepository.findById(memberId);
		
		return member;
	}

	public boolean existMember(MemberDto memberDto) {
		Member member = memberRepository.findByEmail(memberDto.getEmail());
		if (member==null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean equalPassword(MemberDto memberDto) {
		if (memberDto.getPassword().equals(memberDto.getRePassword())) {
			return true;
		} else {
			return false;
		}
	}
	
}
