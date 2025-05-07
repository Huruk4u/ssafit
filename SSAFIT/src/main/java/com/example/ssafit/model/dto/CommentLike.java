package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLike {
    private int commentLikeId;
    private int commentId;
    private int userId;

    public CommentLike() {}

    public CommentLike(int commentLikeId, int commentId, int userId) {
        this.commentLikeId = commentLikeId;
        this.commentId = commentId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "commentLikeId=" + commentLikeId +
                ", commentId=" + commentId +
                ", userId=" + userId +
                '}';
    }

}
