package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDislike {
    private int articleDislikeId;
    private int articleId;
    private int userId;

    private ArticleDislike() {}

    public ArticleDislike(int articleDislikeId, int articleId, int userId) {
        this.articleDislikeId = articleDislikeId;
        this.articleId = articleId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ArticleDislike{" +
                "articleDislikeId=" + articleDislikeId +
                ", articleId=" + articleId +
                ", userId=" + userId +
                '}';
    }
}
