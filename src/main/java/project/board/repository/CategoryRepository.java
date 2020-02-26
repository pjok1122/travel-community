package project.board.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryRepository {

	Long selectIdByTitle(String title);

}
