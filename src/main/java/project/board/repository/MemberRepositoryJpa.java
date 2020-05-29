package project.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Member;

public interface MemberRepositoryJpa extends JpaRepository<Member, Long>{
	Optional<Member> findByEmail(String email);

}
