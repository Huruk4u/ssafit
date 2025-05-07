package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDislike {
    private int commentDislikeId;
    private int commentId;
    private int userId;

    public CommentDislike() {}

    public CommentDislike(int commentDislikeId, int commentId, int userId) {
        this.commentDislikeId = commentDislikeId;
        this.commentId = commentId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CommentDislike{" +
                "commentDislikeId=" + commentDislikeId +
                ", commentId=" + commentId +
                ", userId=" + userId +
                '}';
    }

}
