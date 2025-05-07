package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private int commentId;
    private int articleId;
    private int userId;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Comment() {}

    public Comment(int commentId, int articleId, int userId, String content, String createdAt, String updatedAt) {
        this.commentId = commentId;
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
