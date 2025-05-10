package com.example.ssafit.service;

import com.example.ssafit.model.dto.Comment;
import com.example.ssafit.model.dto.Report;

import java.util.List;

/**
 * Comment Service의 구현 기능
 * 1. 댓글 작성하기
 * 2. 댓글 좋아요 누르기
 * 3. 댓글 싫어요 누르기
 * 4. 댓글 신고하기
 * 5. 댓글 지우기
 * 6. 댓글 수정하기
 * 7. 댓글 조회하기 - 게시글별
 * 8. 댓글 조회하기 - 유저별
 */
public interface CommentService {

    // 댓글 조회 관련 메서드
    List<Comment> searchAllComments();
    Comment searchCommentByCommentId(int commentId);
    List<Comment> searchCommentListByArticleId(int articleId);
    List<Comment> searchCommentListByUserId(int userId);

    // 댓글 작성
    int addComment(Comment comment);

    // 댓글 수정
    int modifyComment(int commentId, Comment comment);

    // 댓글 삭제
    int removeComment(int commentId);

    // 좋아요 관련 메서드
    boolean likeComment(int commentId, int userId);
    int getLikeCount(int commentId);

    // 싫어요 관련 메서드
    boolean dislikeComment(int commentId, int userId);
    int getDislikeCount(int commentId);

    // 신고 관련 메서드
    boolean reportComment(Report report);
}