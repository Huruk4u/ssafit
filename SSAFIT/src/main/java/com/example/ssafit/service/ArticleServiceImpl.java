package com.example.ssafit.service;

import com.example.ssafit.model.dao.ArticleDao;
import com.example.ssafit.model.dto.Article;
import com.example.ssafit.model.dto.SearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Override
    public List<Article> searchAllArticle() {
        return articleDao.selectAllArticle();
    }

    @Override
    public Article searchArticleByArticleId(int articleId) {
        return articleDao.selectArticleByArticleId(articleId);
    }

    @Override
    public List<Article> searchArticleListByUserId(int userId) {
        return articleDao.selectArticleListByUserId(userId);
    }

    @Override
    public List<Article> searchArticleListByArticleLikeUserId(int userId) {
        return articleDao.selectArticleListByArticleLikeUserId(userId);
    }

    @Override
    public List<Article> searchArticleListByBoardId(int boardId) {
        return articleDao.selectArticleListByBoardId(boardId);
    }

    @Override
    public List<Article> searchArticleListByCondition(SearchCondition condition) {
        return articleDao.selectArticleListByCondition(condition);
    }

    @Override
    @Transactional
    public int addArticle(Article article) {
        articleDao.insertArticle(article);
        return 1;
    }

    @Override
    @Transactional
    public int modifyArticle(int articleId, Article article) {
        articleDao.updateArticle(articleId, article);
        return 1;
    }

    @Override
    @Transactional
    public int removeArticle(int articleId) {
        articleDao.deleteArticle(articleId);
        return 1;
    }

    @Override
    @Transactional
    public boolean likeArticle(int articleId, int userId) {
        if (articleDao.isLiked(articleId, userId)) {
            // 이미 좋아요한 경우 - 취소
            articleDao.deleteLike(articleId, userId);
            articleDao.decreaseLikeCount(articleId); // 좋아요 수 감소
            return false;
        } else {
            // 좋아요 추가
            articleDao.insertLike(articleId, userId);
            articleDao.increaseLikeCount(articleId); // 좋아요 수 증가

            // 이미 싫어요를 했다면 싫어요 취소
            if (articleDao.isDisliked(articleId, userId)) {
                articleDao.deleteDislike(articleId, userId);
                articleDao.decreaseDislikeCount(articleId);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean disLikeArticle(int articleId, int userId) {
        if (articleDao.isDisliked(articleId, userId)) {
            // 이미 싫어요한 경우 - 취소
            articleDao.deleteDislike(articleId, userId);
            articleDao.decreaseDislikeCount(articleId); // 싫어요 수 감소
            return false;
        } else {
            // 싫어요 추가
            articleDao.insertDislike(articleId, userId);
            articleDao.increaseDislikeCount(articleId); // 싫어요 수 증가

            // 이미 좋아요를 했다면 좋아요 취소
            if (articleDao.isLiked(articleId, userId)) {
                articleDao.deleteLike(articleId, userId);
                articleDao.decreaseLikeCount(articleId);
            }
            return true;
        }
    }

    @Override
    public int getLikeCount(int articleId) {
        return articleDao.getLikeCount(articleId);
    }

    @Override
    public int getDislikeCount(int articleId) {
        return articleDao.getDislikeCount(articleId);
    }

    @Override
    @Transactional
    public void increaseViewCount(int articleId) {
        articleDao.increaseViewCount(articleId);
    }
}