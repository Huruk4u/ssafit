package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.comment.Comment;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.NotificationService;
import com.example.ssafit.model.service.ReportService;
import com.example.ssafit.model.service.board.ArticleService;
import com.example.ssafit.model.service.board.CommentService;
import com.example.ssafit.model.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 일반 유저가 접근 가능한 Report 관련 기능
 * 1. Report 생성
 */
@RestController
@RequestMapping("/api_report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Article 신고 기능
    @PostMapping("/post/article")
    public ResponseEntity reportArticle(@RequestBody @Valid Report reportData) {
        reportData.setType("ARTICLE");
        int result = reportService.addReport(reportData);
        System.out.println("요청 처리 완료!");
        return new ResponseEntity(result == 1? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    // 댓글 신고 기능
    @PostMapping("/post/comment")
    public ResponseEntity<?> reportComment(@RequestBody @Valid Report reportData) {
        reportData.setType("COMMENT");
        int result = reportService.addReport(reportData);

        return new ResponseEntity(result == 1? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

}
