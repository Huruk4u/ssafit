package com.example.ssafit.model.service.board;

import com.example.ssafit.exception.CustomUnAuthenticationException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dao.ArticleDao;
import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.SearchCondition;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.BadgeService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserService userService;

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
        int result = articleDao.insertArticle(article);
        if (result > 0) {
            badgeService.checkAndAwardArticleBadges(article.getUserId());
        }
        return result;
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
            articleDao.deleteLike(articleId, userId);
            return false;
        } else {
            articleDao.insertLike(articleId, userId);
            if (articleDao.isDisliked(articleId, userId)) {
                articleDao.deleteDislike(articleId, userId);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean disLikeArticle(int articleId, int userId) {
        if (articleDao.isDisliked(articleId, userId)) {
            articleDao.deleteDislike(articleId, userId);
            return false;
        } else {
            articleDao.insertDislike(articleId, userId);
            if (articleDao.isLiked(articleId, userId)) {
                articleDao.deleteLike(articleId, userId);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public void increaseViewCount(int articleId) {
        articleDao.increaseViewCount(articleId);
    }

    @Override
    public int getTotalCount(SearchCondition condition) {
        return articleDao.getTotalCount(condition);
    }

    @Override
    public Map<String, Boolean> getLikeStatus(int articleId, String username) {
        User user = userService.searchByUsername(username);
        if (user == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }
        Map<String, Boolean> status = new HashMap<>();
        status.put("isLiked", articleDao.isLiked(articleId, user.getUserId()));
        status.put("isDisliked", articleDao.isDisliked(articleId, user.getUserId()));
        return status;
    }
}