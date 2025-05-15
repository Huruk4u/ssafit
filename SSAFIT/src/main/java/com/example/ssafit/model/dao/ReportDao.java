package com.example.ssafit.model.dao;

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
public interface ReportDao {

    // 1. 모든 Report 조회
    List<Report> selectAllReports();

    // 2. 조치 안 된 Report 조회
    List<Report> selectReportNotHandled();

    // 2. Report 생성
    void insertReport(Report report);

    // 4. 특정 User에 대한 Report조회
    List<Report> selectReportByUserId(int userId);

    // 5. Report ID로 Report조회
    Report selectReportByReportId(int reportId);

    // 6. ReportId의 action수정
    void updateReportAction(int reportId, String action);
}
