package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.Notification;

import java.util.List;

public interface NotificationService {

    // 댓글 알림 생성
    int createCommentNotification(int articleId, int commentId, int commentAuthorId);

    // 일반 알림 생성
    int addNotification(Notification notification);

    // 사용자별 알림 조회
    List<Notification> getNotificationsByUserId(int userId);

    // 알림 읽음 처리
    int markAsRead(Long notificationId);

    // 사용자의 전체 알림 읽음 처리
    int markAllAsRead(int userId);

    // 알림 삭제
    int removeNotification(Long notificationId);

    // 사용자의 전체 알림 삭제
    int removeAllNotifications(int userId);
}