package com.example.ssafit.model.dto;

import java.time.LocalDateTime;

public class Report {
    private int reportId;
    private int commentId;
    private long userId;
    private String reason;
    private LocalDateTime createdAt;

    public Report() {
    }

    public Report(int reportId, int commentId, long userId, String reason, LocalDateTime createdAt) {
        this.reportId = reportId;
        this.commentId = commentId;
        this.userId = userId;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", commentId=" + commentId +
                ", userId=" + userId +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}