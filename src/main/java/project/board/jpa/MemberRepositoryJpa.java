package project.board.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Member;

public interface MemberRepositoryJpa extends JpaRepository<Member, Long> {

//	Member findByEmail(String email);
	 Optional<Member> findById(Long id);
	Optional<Member> findByEmail(String email);

//	void updatePassword(Member savedMember);
//	void updateLoginDate(Long id);
	
//	Long insert(Member member);
	
//	void delete(Long id);
//	Integer sumGoodCount(Long id);

}
