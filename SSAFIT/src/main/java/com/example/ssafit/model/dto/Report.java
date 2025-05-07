package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * //TODO
 * 1. 신고자 ID, 피신고자 ID 두 개가 다 필요하지 않을까?
 */
@Getter
@Setter
public class Report {
    private int reportId;
    private String reportCategory;
    private int reportedUserId;
    private int reportedArticleId;
    private int reportedCommentId;
    private String content;
    private String createdAt;

    public Report() {}

    public Report(int reportId, String reportCategory, int reportedUserId, int reportedArticleId, int reportedCommentId, String content, String createdAt) {
        this.reportId = reportId;
        this.reportCategory = reportCategory;
        this.reportedUserId = reportedUserId;
        this.reportedArticleId = reportedArticleId;
        this.reportedCommentId = reportedCommentId;
        this.content = content;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reportCategory='" + reportCategory + '\'' +
                ", reportedUserId=" + reportedUserId +
                ", reportedArticleId=" + reportedArticleId +
                ", reportedCommentId=" + reportedCommentId +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
