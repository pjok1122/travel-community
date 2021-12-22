package project.board.repository.jpa;

import org.apache.ibatis.annotations.Mapper;
import project.board.domain.Member;

public interface MemberRepositoryJpa  {

	Member findByEmail(String email);
	Member findById(Long id);

	void updatePassword(Member savedMember);
	void updateLoginDate(Long id);
	
	Long insert(Member member);
	
	void delete(Long id);
	Integer sumGoodCount(Long id);

}
