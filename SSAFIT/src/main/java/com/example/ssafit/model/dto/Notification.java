package com.example.ssafit.model.dto;

import java.time.LocalDateTime;

public class Notification {
    private Long notificationId;
    private Long userId;            // 알림을 받을 사용자 ID
    private String type;            // 알림 유형 (comment, like, challenge 등)
    private String payload;         // JSON 형태의 페이로드 데이터
    private boolean isRead;         // 읽음 여부
    private LocalDateTime createdAt; // 생성 시간

    public Notification() {
    }

    public Notification(Long userId, String type, String payload) {
        this.userId = userId;
        this.type = type;
        this.payload = payload;
        this.isRead = false;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", payload='" + payload + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}