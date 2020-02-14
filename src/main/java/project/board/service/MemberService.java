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
		System.out.println(memberDto.getEmail());
		Member savedMember = memberRepository.findByEmail(memberDto.getEmail());
		if (savedMember == null)
			return null;
		if (passwordCompare(memberDto, savedMember)) {
			memberRepository.updateLoginDate(savedMember.getId());
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
		String salt = UUID.randomUUID().toString();
		
		String hashPassword = sha256Utils.sha256(memberDto.getPassword(), salt);
		Member member = Member.builder().email(memberDto.getEmail())
						.password(hashPassword)
						.salt(salt).build();
		
		memberRepository.insert(member);
		
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

	public void update(@Valid MemberDto memberDto) {
		if(equalPassword(memberDto)) {
			Member member = memberRepository.findByEmail(memberDto.getEmail());
			String hashPassword = sha256Utils.sha256(memberDto.getPassword(), member.getSalt());
			member.setPassword(hashPassword);
			memberRepository.updatePassword(member);
			
		}
		
	}

	public Member getMyInfo(Long id) {
		
		return memberRepository.findById(id);
	}

	public int getGoodCount(Long id) {
		Integer goodCnt = memberRepository.sumGoodCount(id);
		if (goodCnt == null) goodCnt = 0;
		return goodCnt;
	}

	public void delete(Long memberId) {
		memberRepository.delete(memberId);
	}
	
}
