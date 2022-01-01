package project.board.params;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Data;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@Builder
public class ArticleSearchParam {

    private Category category;
    private Nation nation;
    private String searchText;

    @Builder.Default
    private ArticleStatus status = ArticleStatus.PERMANENT;

    @Builder.Default
    private Pageable pageable = PageRequest.of(0, 10);
}
