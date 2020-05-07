package project.board.service;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.repository.MemberRepositoryJpa;
import project.board.util.Sha256Utils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa {

	private final MemberRepositoryJpa memberRepository;
	private final Sha256Utils shaUtils;
	
	
	/**
	 * 이메일과 비밀번호로 회원을 생성한다.
	 * 검증로직이 필요한 경우 MemberRegisterValidator.validate()를 사용한다.
	 * 
	 * @param email 
	 * @param password 
	 * @return Member
	 */
	@Transactional
	public Member save(String email, String password) {

		String salt = UUID.randomUUID().toString();
		String hashPassword = shaUtils.sha256(password, salt);
		Member member = Member.builder().email(email)
							.password(hashPassword)
							.salt(salt).build();
		
		memberRepository.save(member);
		
		return member;
	}
	
	/**
	 * 이메일로 회원을 조회한다.
	 * @param email
	 * @return Optional<Member>
	 */
	
	public Optional<Member> findOne(String email) {
		return memberRepository.findByEmail(email);
	}

	/**
	 * 이메일과 패스워드로 로그인을 시도한다.
	 * 로그인에 실패하면 null을 반환하고 로그인에 성공하면 Member를 반환한다.
	 * @param email
	 * @param password
	 * @return Member
	 */
	
	@Transactional
	public Member login(String email, String password) {
		Member findMember = memberRepository.findByEmail(email).orElse(null);
		
		System.out.println(0);
		
		//존재하지 않는 유저인 경우
		if(findMember == null) return null;

		System.out.println(1);
		
		//비밀번호가 일치하지 않는 경우
		if(!isPasswordCorrect(findMember, password)) return null;
		
		System.out.println(2);
		
		//로그인 시간 변경
		findMember.updateLoginDate();
		
		return findMember;
	}
	
	private Boolean isPasswordCorrect(Member member, String password) {
		return member.getPassword().equals(shaUtils.sha256(password, member.getSalt())) ? true : false; 
	}
	
	
	

	
}
