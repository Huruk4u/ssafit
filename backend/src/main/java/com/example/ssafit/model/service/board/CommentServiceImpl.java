package com.example.ssafit.model.service.board;

import com.example.ssafit.exception.CustomUnAuthenticationException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dao.CommentDao;
import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.NotificationService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public List<Comment> searchAllComments() {
        return commentDao.selectAllComments();
    }

    @Override
    public Comment searchCommentByCommentId(int commentId) {
        return commentDao.selectCommentByCommentId(commentId);
    }

    @Override
    public List<Comment> searchCommentListByArticleId(int articleId) {
        return commentDao.selectCommentListByArticleId(articleId);
    }

    @Override
    public List<Comment> searchCommentListByUserId(long userId) {
        return commentDao.selectCommentListByUserId(userId);
    }

    @Override
    @Transactional
    public int addComment(Comment comment) {
        int result = commentDao.insertComment(comment);
        if (result > 0 && comment.getCommentId() > 0) {
            notificationService.createCommentNotification(
                    comment.getArticleId(),
                    comment.getCommentId(),
                    comment.getUserId()
            );
        }
        return result;
    }

    @Override
    @Transactional
    public int modifyComment(int commentId, Comment comment) {
        return commentDao.updateComment(commentId, comment);
    }

    @Override
    @Transactional
    public int removeComment(int commentId) {
        return commentDao.deleteComment(commentId);
    }

    @Override
    @Transactional
    public boolean likeComment(int commentId, long userId) {
        if (commentDao.isLiked(commentId, userId)) {
            commentDao.deleteLike(commentId, userId);
            return false;
        } else {
            commentDao.insertLike(commentId, userId);
            if (commentDao.isDisliked(commentId, userId)) {
                commentDao.deleteDislike(commentId, userId);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean dislikeComment(int commentId, long userId) {
        if (commentDao.isDisliked(commentId, userId)) {
            commentDao.deleteDislike(commentId, userId);
            return false;
        } else {
            commentDao.insertDislike(commentId, userId);
            if (commentDao.isLiked(commentId, userId)) {
                commentDao.deleteLike(commentId, userId);
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean reportComment(Report report) {
        commentDao.insertReport(report);
        return true;
    }

    @Override
    public Map<String, Boolean> getLikeStatus(int commentId, String username) {
        User user = userService.searchByUsername(username);
        if (user == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }
        Map<String, Boolean> status = new HashMap<>();
        status.put("isLiked", commentDao.isLiked(commentId, (long) user.getUserId()));
        status.put("isDisliked", commentDao.isDisliked(commentId, (long) user.getUserId()));
        return status;
    }
}