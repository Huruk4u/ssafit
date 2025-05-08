package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Article;
import com.example.ssafit.model.dto.SearchCondition;

import java.util.List;

/**
 * Article Controller의 구현 기능
 *  1. 모든 글들을 조회
 *  2. articleId가 일치하는 글을 조회
 *  3. userId가 일치하는 글들을 조회
 *  4. ArticleLike의 userId가 일치하는 글들을 조회
 *  5. boardId가 일치하는 글들을 조회
 *  6. searchForm으로 글들을 조회
 *  7. article 작성
 *  8. article 수정
 *  9. article 삭제
 */
public interface ArticleDao {

    // 1. 모든 글들을 조회
    List<Article> selectAllArticle();

    // 2. articleId가 일치하는 글을 조회
    Article selectArticleByArticleId(int articleId);

    // 3. userId가 일치하는 글들을 조회
    List<Article> selectArticleListByUserId(int userId);

    // 4. ArticleLike의 userId가 일치하는 글들을 조회
    List<Article> selectArticleListByArticleLikeUserId(int userId);

    // 5. boardId가 일치하는 글들을 조회
    List<Article> selectArticleListByBoardId(int boardId);

    // 6. searchForm으로 글들을 조회
    List<Article> selectArticleListByCondition(SearchCondition condition);

    // 7. article 작성
    int insertArticle(Article article);

    // 8. article 수정
    int updateArticle(int articleId, Article article);

    // 9. article 삭제
    int deleteArticle(int articleId);
}
