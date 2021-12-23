package project.board.service.dto;

import lombok.Builder;
import lombok.Data;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@Builder
public class ArticleRegisterParam {
    private Long id;
    private Long memberId;
    private Category category;
    private Nation nation;
    private String title;
    private String content;
    private String images;
}
