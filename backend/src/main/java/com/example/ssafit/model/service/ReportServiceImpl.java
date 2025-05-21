package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.ReportDao;
import com.example.ssafit.model.dto.Report;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao reportDao;

    @Override
    public List<Report> searchAllReports() {
        return reportDao.selectAllReports();
    }

    @Override
    public List<Report> searchReportNotHandled() {
        return reportDao.selectReportNotHandled();
    }

    @Override
    @Transactional
    public int addReport(Report report) {
        // 자기 자신을 신고한 경우, return 0
        if (report.getReporterId() == report.getReporteeId()) return 0;

        // 신고자, 피신고자, 신고글 ID가 일치하는 신고가 있으면, 추가하지 않음.
        if (searchReportCntByReportInfo(report) > 0) return 0;

        reportDao.insertReport(report);
        return 1;
    }

    @Override
    public List<Report> searchReportByUserId(int userId) {
        return reportDao.selectReportByUserId(userId);
    }

    @Override
    public Report searchReportByReportId(int reportId) {
        return reportDao.selectReportByReportId(reportId);
    }

    @Override
    @Transactional
    public int modifyReportAction(int reportId, int action) {
        System.out.println("report 처리한다! : " + reportId + " " + action);
        reportDao.updateReportAction(reportId, action);
        return 1;
    }

    private int searchReportCntByReportInfo(Report report) {
        return reportDao.selectReportCntByReportInfo(report);
    }
}
