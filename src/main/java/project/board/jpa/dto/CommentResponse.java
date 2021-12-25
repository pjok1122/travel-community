package project.board.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.board.entity.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;

    private LocalDateTime registerDate;
    private LocalDateTime updateDate;

    private int good;

    private String writer;

    private Boolean isGood;

    private List<CommentResponse> replies = new ArrayList<>();

    public CommentResponse(Comment comment, boolean isGood) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.registerDate = comment.getRegisterDate();
        this.updateDate = comment.getUpdateDate();
        this.good = comment.getGood();
        this.writer = comment.getMember().getEmail();
        this.isGood = isGood;
    }
}
