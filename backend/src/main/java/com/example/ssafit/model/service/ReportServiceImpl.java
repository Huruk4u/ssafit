package com.example.ssafit.model.service;

import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dao.ReportDao;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private UserService userService;

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
        if (report.getReporterId() == report.getReporteeId()) {
            throw new CustomBusinessException(ErrorCode.SELF_REPORTED);
        }

        // 신고자, 피신고자, 신고글 ID가 일치하는 신고가 있으면, 추가하지 않음.
        if (searchReportCntByReportInfo(report) > 0) {
            throw new CustomBusinessException(ErrorCode.DUPLICATED_REPORT);
        }

        User reportee = userService.searchByUserId(report.getReporteeId());
        if (reportee.getRole().equals("ROLE_ADMIN")) {
            throw new CustomBusinessException(ErrorCode.ADMIN_REPORTED);
        }

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

        // 이미 처리된 신고에 대해선 처리하지 않음.
        Report report = reportDao.selectReportByReportId(reportId);
        if (report.getAction() != 0) throw new CustomBusinessException(ErrorCode.ALREADY_HANDLED);

        reportDao.updateReportAction(reportId, action);

        return 1;
    }

    private int searchReportCntByReportInfo(Report reportData) {
        return reportDao.selectReportCntByReportInfo(reportData);
    }
}
