package com.example.ssafit.controller;

import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.CustomUnAuthenticationException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.board.CommentService;
import com.example.ssafit.model.service.NotificationService;
import com.example.ssafit.model.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api_comment")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "Comment", description = "Comment API")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    // 게시글별 댓글 조회
    @GetMapping("/list")
    public ResponseEntity<List<Comment>> getCommentsByArticleId(@RequestParam("article_id") int articleId) {
        List<Comment> comments = commentService.searchCommentListByArticleId(articleId);
        if (comments == null || comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    // 댓글 작성
    @PostMapping("/write")
    public ResponseEntity<?> writeComment(
            @RequestParam("article_id") int articleId,
            @RequestBody Comment commentData,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);

        // 댓글 객체 설정 - 실제 로그인한 사용자 ID로 설정
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(currentUser.getUserId());
        comment.setContent(commentData.getContent());

        int result = commentService.addComment(comment);
        if (result == -1) throw new CustomBusinessException(ErrorCode.COMMENT_CREATE_FAILED);

        // 댓글 작성 성공 시 알림 생성
        notificationService.createCommentNotification(articleId, comment.getCommentId(), currentUser.getUserId());

        return ResponseEntity.ok(HttpStatus.CREATED.value());
    }

    // 댓글 수정
    @PutMapping("/put/comment_id/{commentId}")
    public ResponseEntity<?> modifyComment(
            @PathVariable("commentId") int commentId,
            @RequestBody Comment comment,
            Principal principal) {

        String currentUsername = principal.getName();

        // 기존 댓글 조회
        Comment originalComment = commentService.searchCommentByCommentId(commentId);
        if (originalComment == null) throw new CustomBusinessException(ErrorCode.COMMENT_NOT_FOUND);

        // 작성자 정보 조회
        User author = userService.searchByUserId((int) originalComment.getUserId());
        if (author == null) throw new CustomBusinessException(ErrorCode.COMMENT_AUTHOR_NOT_FOUND);

        // 로그인 사용자와 작성자가 다르면 수정 불가
        if (!author.getUserName().equals(currentUsername)) {
            throw new CustomBusinessException(ErrorCode.BAD_APPROACH);
        }

        // 수정 진행
        int result = commentService.modifyComment(commentId, comment);
        if (result == -1) throw new CustomBusinessException(ErrorCode.COMMENT_MODIFY_FAILED);

        return ResponseEntity.ok(HttpStatus.ACCEPTED.value());
    }

    // 댓글 삭제
    @DeleteMapping("/delete/comment_id/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") int commentId,
            Principal principal) {

        String currentUsername = principal.getName();

        // 댓글 조회
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) throw new CustomBusinessException(ErrorCode.COMMENT_NOT_FOUND);

        // 작성자 정보 조회
        User author = userService.searchByUserId(comment.getUserId());
        if (author == null) throw new CustomBusinessException(ErrorCode.COMMENT_AUTHOR_NOT_FOUND);

        // 로그인 사용자와 작성자가 다르면 삭제 불가
        if (!author.getUserName().equals(currentUsername)) throw new CustomBusinessException(ErrorCode.BAD_APPROACH);

        // 삭제 진행
        int result = commentService.removeComment(commentId);
        if (result == -1) throw new CustomBusinessException(ErrorCode.COMMENT_REMOVE_FAILED);

        return ResponseEntity.ok(HttpStatus.NO_CONTENT.value());
    }


    // 댓글 좋아요
    @PostMapping("/like")
    public ResponseEntity<?> likeComment(
            @RequestParam("comment_id") int commentId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);

        // 댓글 존재 여부 확인
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) throw new CustomBusinessException(ErrorCode.COMMENT_NOT_FOUND);

        boolean result = commentService.likeComment(commentId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }

    // 댓글 싫어요
    @PostMapping("/dislike")
    public ResponseEntity<?> dislikeComment(
            @RequestParam("comment_id") int commentId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);

        // 댓글 존재 여부 확인
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) throw new CustomBusinessException(ErrorCode.COMMENT_NOT_FOUND);

        boolean result = commentService.dislikeComment(commentId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }
}