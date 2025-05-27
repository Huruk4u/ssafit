package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.Report;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentDao {

    // 댓글 조회 관련 메서드
    List<Comment> selectAllComments();
    Comment selectCommentByCommentId(int commentId);
    List<Comment> selectCommentListByArticleId(int articleId);
    List<Comment> selectCommentListByUserId(long userId);

    // 댓글 작성
    int insertComment(Comment comment);

    // 댓글 수정
    int updateComment(@Param("commentId") int commentId, @Param("comment") Comment comment);

    // 댓글 삭제
    int deleteComment(int commentId);

    // 좋아요 관련 메서드
    boolean isLiked(@Param("commentId") int commentId, @Param("userId") Long userId);
    void insertLike(@Param("commentId") int commentId, @Param("userId") Long userId);
    void deleteLike(@Param("commentId") int commentId, @Param("userId") Long userId);

    // 싫어요 관련 메서드
    boolean isDisliked(@Param("commentId") int commentId, @Param("userId") Long userId);
    void insertDislike(@Param("commentId") int commentId, @Param("userId") Long userId);
    void deleteDislike(@Param("commentId") int commentId, @Param("userId") Long userId);

    // 신고 관련 메서드
    int insertReport(Report report);
    boolean isReported(@Param("commentId") int commentId, @Param("userId") Long userId);
}