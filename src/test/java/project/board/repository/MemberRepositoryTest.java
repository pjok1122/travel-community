package project.board.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import project.board.domain.Member;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	private Member savedMember;


	@BeforeEach
	void beforeAll() {
		savedMember = CreateDomain.getMember();
		memberRepository.insert(savedMember);
	}

	@Test
	void findByEmail() {
		Member result = memberRepository.findByEmail(savedMember.getEmail());

		assertThat(result.getId()).isEqualTo(savedMember.getId());
	}

	@Test
	void updatePassword() {
		Member member = savedMember;
		member.setPassword("newPasswd");

		memberRepository.updatePassword(member);
		Member result = memberRepository.findById(member.getId());

		assertThat(result.getPassword()).isEqualTo(member.getPassword());
	}

	@Test
	void updateLoginDate() {
		Member member = savedMember;

		memberRepository.updateLoginDate(member.getId());
		Member result = memberRepository.findById(member.getId());

		assertThat(result.getLoginDate()).isAfter(member.getLoginDate());
	}

	@Test
	void sumGoodCount() {
		Integer result = memberRepository.sumGoodCount(savedMember.getId());

		assertThat(result).isNull();
	}



}