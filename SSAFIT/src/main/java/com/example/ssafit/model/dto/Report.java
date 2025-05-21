package com.example.ssafit.model.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class Report {

    private int reportId;

    @NotNull
    private int reporterId;

    @NotNull
    private int reporteeId;

    @NotNull
    private String reportCategory;

    private String type;

    @NotNull
    private int articleId;

    // Report의 처리 여부
    private boolean isHandled;

    private String content;
    private String action;
    private LocalDateTime createdAt;

    public Report() {}

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public int getReporteeId() {
        return reporteeId;
    }

    public void setReporteeId(int reporteeId) {
        this.reporteeId = reporteeId;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isHandled() {
        return isHandled;
    }

    public void setHandled(boolean handled) {
        isHandled = handled;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reporterId=" + reporterId +
                ", reporteeId=" + reporteeId +
                ", reportCategory='" + reportCategory + '\'' +
                ", type='" + type + '\'' +
                ", articleId=" + articleId +
                ", isHandled=" + isHandled +
                ", content='" + content + '\'' +
                ", action='" + action + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}