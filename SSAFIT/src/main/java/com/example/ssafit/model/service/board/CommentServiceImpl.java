package com.example.ssafit.model.service.board;

import com.example.ssafit.model.dao.CommentDao;
import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private NotificationService notificationService;

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
    public List<Comment> searchCommentListByUserId(int userId) {
        return commentDao.selectCommentListByUserId(userId);
    }

    @Override
    @Transactional
    public int addComment(Comment comment) {
        int result = commentDao.insertComment(comment);

        // 댓글 저장 성공 및 댓글 ID가 설정된 경우 알림 생성
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
    public boolean likeComment(int commentId, int userId) {
        if (commentDao.isLiked(commentId, (long) userId)) {
            // 이미 좋아요한 경우 - 취소
            commentDao.deleteLike(commentId, userId);
            commentDao.decreaseLikeCount(commentId); // 좋아요 수 감소
            return false;
        } else {
            // 좋아요 추가
            commentDao.insertLike(commentId, userId);
            commentDao.increaseLikeCount(commentId); // 좋아요 수 증가

            // 이미 싫어요를 했다면 싫어요 취소
            if (commentDao.isDisliked(commentId, userId)) {
                commentDao.deleteDislike(commentId, userId);
                commentDao.decreaseDislikeCount(commentId);
            }
            return true;
        }
    }

    @Override
    public int getLikeCount(int commentId) {
        return commentDao.getLikeCount(commentId);
    }

    @Override
    @Transactional
    public boolean dislikeComment(int commentId, int userId) {
        if (commentDao.isDisliked(commentId, userId)) {
            // 이미 싫어요한 경우 - 취소
            commentDao.deleteDislike(commentId, userId);
            commentDao.decreaseDislikeCount(commentId); // 싫어요 수 감소
            return false;
        } else {
            // 싫어요 추가
            commentDao.insertDislike(commentId, userId);
            commentDao.increaseDislikeCount(commentId); // 싫어요 수 증가

            // 이미 좋아요를 했다면 좋아요 취소
            if (commentDao.isLiked(commentId, (long) userId)) {
                commentDao.deleteLike(commentId, userId);
                commentDao.decreaseLikeCount(commentId);
            }
            return true;
        }
    }

    @Override
    public int getDislikeCount(int commentId) {
        return commentDao.getDislikeCount(commentId);
    }

    @Override
    @Transactional
    public boolean reportComment(Report report) {
        // 신고 추가
        commentDao.insertReport(report);
        return true;
    }
}