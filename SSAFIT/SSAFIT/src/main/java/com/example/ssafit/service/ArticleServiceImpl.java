package com.example.ssafit.service;

import com.example.ssafit.model.dao.ArticleDao;
import com.example.ssafit.model.dto.Article;
import com.example.ssafit.model.dto.SearchCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}