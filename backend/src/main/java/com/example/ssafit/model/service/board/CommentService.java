package com.example.ssafit.model.service.board;

import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.Report;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Comment> searchAllComments();
    Comment searchCommentByCommentId(int commentId);
    List<Comment> searchCommentListByArticleId(int articleId);
    List<Comment> searchCommentListByUserId(long userId);

    int addComment(Comment comment);
    int modifyComment(int commentId, Comment comment);
    int removeComment(int commentId);

    boolean likeComment(int commentId, long userId);
    boolean dislikeComment(int commentId, long userId);

    boolean reportComment(Report report);
    Map<String, Boolean> getLikeStatus(int commentId, String username);
}