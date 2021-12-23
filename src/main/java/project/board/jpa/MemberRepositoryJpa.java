package project.board.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.board.entity.Member;

public interface MemberRepositoryJpa extends JpaRepository<Member, Long> {

	Optional<Member> findById(Long id);
	Optional<Member> findByEmail(String email);

	@Query(value = "SELECT sum(good) "
				   + "FROM("
				   + "SELECT good \n"
				   + "FROM article a \n"
				   + "WHERE a.member_id = :id \n"
				   + "UNION ALL \n"
				   + "SELECT good \n"
				   + "FROM comment c \n"
				   + "WHERE c.member_id = :id \n"
				   + ") b", nativeQuery = true)
	Long countGoodById(Long id);

}
