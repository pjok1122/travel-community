package project.board.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Member;
import project.board.entity.dto.MyInfoDto;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.CommentRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;
import project.board.util.Sha256Utils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa {

	private final MemberRepositoryJpa memberRepository;
	private final ArticleRepositoryJpa articleRepository;
	private final CommentRepositoryJpa commentRepository;
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
	public Long save(String email, String password) {
		Member member = Member.createMember(email, password);
		memberRepository.save(member);
		return member.getId();
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
		
		//존재하지 않는 유저인 경우
		if(findMember == null) return null;

		//비밀번호가 일치하지 않는 경우
		if(!isPasswordCorrect(findMember, password)) return null;
		
		//로그인 시간 변경
		findMember.updateLoginDate();
		
		return findMember;
	}
	
	private Boolean isPasswordCorrect(Member member, String password) {
		return member.getPassword().equals(shaUtils.sha256(password, member.getSalt())) ? true : false; 
	}

	/**
	 * 회원 정보를 Dto로 반환한다.
	 * @param memberId
	 * @return MyInfoDto
	 */
	public MyInfoDto getMyInfo(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException(memberId + "is not exist"));
		int likeCount = articleRepository.countByMember(member) + commentRepository.countByMember(member);
		
		return new MyInfoDto(member, likeCount);
	}
	
	
	

	
}
