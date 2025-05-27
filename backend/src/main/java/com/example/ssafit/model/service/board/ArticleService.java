package com.example.ssafit.model.service.board;

import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.SearchCondition;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    List<Article> searchAllArticle();
    Article searchArticleByArticleId(int articleId);
    List<Article> searchArticleListByUserId(int userId);
    List<Article> searchArticleListByArticleLikeUserId(int userId);
    List<Article> searchArticleListByBoardId(int boardId);
    List<Article> searchArticleListByCondition(SearchCondition condition);
    int addArticle(Article article);
    int modifyArticle(int articleId, Article article);
    int removeArticle(int articleId);

    boolean likeArticle(int articleId, int userId);
    boolean disLikeArticle(int articleId, int userId);
    void increaseViewCount(int articleId);

    int getTotalCount(SearchCondition condition);

    Map<String, Boolean> getLikeStatus(int articleId, String username);
}