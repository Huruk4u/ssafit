package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.ArticleDao;
import com.example.ssafit.model.service.ArticleService;
import com.example.ssafit.model.dto.Article;
import com.example.ssafit.model.dto.SearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // constructor injection을 위한 어노테이션
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao;

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
        return articleDao.insertArticle(article);
    }

    @Override
    @Transactional
    public int modifyArticle(int articleId, Article article) {
        return articleDao.updateArticle(articleId, article);
    }

    @Override
    @Transactional
    public int removeArticle(int articleId) {
        return articleDao.deleteArticle(articleId);
    }
}
