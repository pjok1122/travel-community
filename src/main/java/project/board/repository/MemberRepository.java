package project.board.repository;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Member;

@Mapper
public interface MemberRepository {

	Member findByEmail(String email);

	void update(Member savedMember);
	
	Long insert(Member member);
	
	void delete(Long id);
}
