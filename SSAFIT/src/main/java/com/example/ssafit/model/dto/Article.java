package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TODO
 * 임시 수정을 가한 field
 * tag : 여러개의 String을 List형식으로 포함한 구조로 하는 게 더 좋을 것 같음.
 * SQL에선 이걸 어떤 형식으로 표현해야할 지..
 */
@Setter
@Getter
public class Article {
    private int articleId;
    private int userId;
    private int boardId;
    private String title;
    private String content;
    private List<String> tag;
    private String createdAt;
    private String updatedAt;

    public Article() {}

    public Article(int articleId, int userId, int boardId, String title, String content, List<String> tag, String createdAt, String updatedAt) {
        this.articleId = articleId;
        this.userId = userId;
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
