package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleLike {
    private int articleLikeId;
    private int userId;
    private int articleId;

    public ArticleLike() {}

    public ArticleLike(int articleLikeId, int userId, int articleId) {
        this.articleLikeId = articleLikeId;
        this.userId = userId;
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "ArticleLike{" +
                "articleLikeId=" + articleLikeId +
                ", userId=" + userId +
                ", articleId=" + articleId +
                '}';
    }
}
