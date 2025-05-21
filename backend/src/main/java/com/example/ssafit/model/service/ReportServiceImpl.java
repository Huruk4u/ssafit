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
}
