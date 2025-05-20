package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.service.NotificationService;
import com.example.ssafit.model.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 일반 유저가 접근 가능한 Report 관련 기능
 * 1. Report 생성
 */
@RestController
@RequestMapping("/api_report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private NotificationService notificationService;

    // Report 생성 기능
    @PostMapping("/post")
    public ResponseEntity reportUser(@RequestBody @Valid Report report) {
        int result = reportService.addReport(report);
        if (result == 1) {
            notificationService.createReportNotification(report.getReportId(), report.getReporteeId(), report.getReporterId());
        }
        return new ResponseEntity(result == 1? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

}
