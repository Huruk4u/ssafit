package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.Report;

import java.util.List;

/**
 * 1. 모든 Report 조회
 * 2. Report생성
 * 3. Report ID로 Report 삭제
 * 4. 특정 User에 대한 Report조회
 * 5. Report ID로 Report조회
 * 6. Report ID로 action 수정
 */
public interface ReportService {

    // 1. 모든 Report 조회
    List<Report> searchAllReports();

    // 2. 조치 안 된 Report만 조회
    List<Report> searchReportNotHandled();

    // 2. Report 생성
    int addReport(Report report);

    // 4. 특정 User에 대한 Report조회
    List<Report> searchReportByUserId(int userId);

    // 5. Report ID로 Report조회
    Report searchReportByReportId(int reportId);

    // 6. ReportId의 action수정
    int modifyReportAction(int reportId, String action);
}
