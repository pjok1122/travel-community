package project.board.service;

import java.time.LocalDateTime;

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
	
}
