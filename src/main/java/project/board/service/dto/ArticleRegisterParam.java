package project.board.service.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import project.board.enums.ArticleStatus;
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
    private List<String> images;
    private ArticleStatus status;
}
