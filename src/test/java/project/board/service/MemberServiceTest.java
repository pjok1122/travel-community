package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.entity.Member;
import project.board.entity.dto.MyInfoDto;
import project.board.repository.MemberRepositoryJpa;
import project.board.util.Sha256Utils;

@SpringBootTest
@Transactional
public class MemberServiceTest {
	
	@Autowired MemberServiceJpa memberService;
	@Autowired MemberRepositoryJpa memberRepository;
	
	private Member createMember(String email, String password) {
		Long id = memberService.save(email, password);
		return memberRepository.findById(id).get();
	}
	
	/**
	 * save() Test
	 * 회원 저장이 정상적으로 동작하는지 확인한다.
	 * 비밀번호에 해시값으로 잘 저장되는지 확인한다.
	 */
	@Test
	public void 멤버_생성() throws Exception {
		//given
		String email = "email";
		String pw = "password";

		//when
		memberService.save(email, pw);
		Member member = memberService.findOne(email).orElseThrow(()->new Exception("Doesn't exist email"));
		
		//then
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getSalt()).isNotBlank();
		assertThat(member.getPassword()).isEqualTo(Sha256Utils.sha256(pw, member.getSalt()));
	}
	
	/**
	 * login() Test
	 * 로그인이 정상적으로 동작하는지 확인한다.
	 */
	@Test
	public void 로그인() {
		//given
		createMember("email", "password");
		
		//when
		Member member = memberService.login("email", "password");
		
		//then
		assertThat(member).isNotNull();
	}
	
	/**
	 * getMyInfo() Test
	 * 마이 페이지의 내 정보를 정상적으로 불러올 수 있는지 확인한다.
	 * 좋아요 개수가 정상적으로 나오는지 확인하는 테스트한다.
	 */
	@Test
	public void 마이페이지_내정보() {
		//given
		Member member = createMember("email", "password");

		//when
		MyInfoDto myInfo = memberService.getMyInfo(member.getId());
		
		//then
		assertThat(myInfo.getEmail()).isEqualTo("email");
		assertThat(myInfo.getLikeCount()).isEqualTo(0);
	}
}
