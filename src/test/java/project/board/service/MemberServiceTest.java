package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.entity.Member;
import project.board.util.Sha256Utils;

@SpringBootTest
@Transactional
public class MemberServiceTest {
	
	@Autowired MemberServiceJpa memberService;
	@Autowired Sha256Utils shaUtils;
	
	/**
	 * ID, PW를 넘겨주었을 때, 비밀번호에 해시값이 잘 저장되는지 확인한다.
	 */
	@Test
	public void 멤버_생성() throws Exception {
		//given
		String email = "email";
		String pw = "password";
		memberService.save(email, pw);
		
		//when
		Member member = memberService.findOne(email).orElseThrow(()->new Exception("Doesn't exist email"));
		
		//then
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getSalt()).isNotBlank();
		assertThat(member.getPassword()).isEqualTo(shaUtils.sha256(pw, member.getSalt()));
	}
	
	@Test
	public void 로그인() {
		//given
		String email = "email";
		String pw = "password";
		Member saveMember = memberService.save(email, pw);
		
		System.out.println(saveMember.getPassword());
		System.out.println(saveMember.getSalt());
		
		//when
		Member member = memberService.login(email, pw);
		
		//then
		assertThat(member).isNotNull();
	}
}
