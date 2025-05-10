package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Comment;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.User;
import com.example.ssafit.service.CommentService;
import com.example.ssafit.service.UserService;
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
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 객체 설정 - 실제 로그인한 사용자 ID로 설정
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(currentUser.getUserId());
        comment.setContent(commentData.getContent());

        int result = commentService.addComment(comment);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
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
        if (originalComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 존재하지 않습니다.");
        }

        // 작성자 정보 조회
        User author = userService.findUserById((int) originalComment.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다르면 수정 불가
        if (!author.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }

        // 수정 진행
        int result = commentService.modifyComment(commentId, comment);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/comment_id/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") int commentId,
            Principal principal) {

        String currentUsername = principal.getName();

        // 댓글 조회
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 존재하지 않습니다.");
        }

        // 작성자 정보 조회
        User author = userService.findUserById(comment.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다르면 삭제 불가
        if (!author.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        // 삭제 진행
        int result = commentService.removeComment(commentId);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    // 댓글 좋아요
    @PostMapping("/like")
    public ResponseEntity<?> likeComment(
            @RequestParam("comment_id") int commentId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 존재 여부 확인
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }

        boolean result = commentService.likeComment(commentId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }

    // 댓글 싫어요
    @PostMapping("/dislike")
    public ResponseEntity<?> dislikeComment(
            @RequestParam("comment_id") int commentId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 존재 여부 확인
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }

        boolean result = commentService.dislikeComment(commentId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }

    // 댓글 신고
    @PostMapping("/report")
    public ResponseEntity<?> reportComment(
            @RequestParam("comment_id") int commentId,
            @RequestBody Report reportData,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.findUserByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 댓글 존재 여부 확인
        Comment comment = commentService.searchCommentByCommentId(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }

        // 신고 객체 설정 - 실제 로그인한 사용자 ID로 설정
        Report report = new Report();
        report.setCommentId(commentId);
        report.setUserId(currentUser.getUserId());
        report.setReason(reportData.getReason());

        boolean result = commentService.reportComment(report);
        if (!result) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신고한 댓글입니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("신고가 접수되었습니다.");
    }
}