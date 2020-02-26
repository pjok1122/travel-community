package project.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import project.board.domain.Member;
import project.board.util.Sha256Utils;

@RunWith(SpringRunner.class)
@MybatisTest
@Import(Sha256Utils.class)
public class MemberRepositoryTest {
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private Sha256Utils sha256Utils;
	
	@Test
	public void crud_test() {
		//삽입
		Member member = Member.builder().email("pjok1122@naver.com").salt("salt")
		.password(sha256Utils.sha256("password", "salt")).build();
		memberRepository.insert(member);
		assertThat(member.getId()).isNotNull();
		
		//조회
		Member member2 = memberRepository.findByEmail("pjok1122@naver.com");
		assertThat(member2.getSalt()).isEqualTo("salt");
		
		//변경
		member2.setPassword(sha256Utils.sha256("password2", "salt"));
		memberRepository.updatePassword(member2);
		
		//삭제
		memberRepository.delete(member2.getId());
		Member deletedMember = memberRepository.findByEmail("pjok1122@naver.com");
		assertThat(deletedMember).isNull();

		
		
		
	}
	
}