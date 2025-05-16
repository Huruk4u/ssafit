package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationDao {

    // 알림 추가
    int insertNotification(Notification notification);

    // 사용자별 알림 조회
    List<Notification> selectNotificationsByUserId(int userId);

    // 알림 상세 조회
    Notification selectNotificationById(Long notificationId);

    // 알림 읽음 처리
    int markNotificationAsRead(Long notificationId);

    // 사용자의 전체 알림 읽음 처리
    int markAllNotificationsAsRead(int userId);

    // 알림 삭제
    int deleteNotification(Long notificationId);

    // 사용자의 전체 알림 삭제
    int deleteAllNotificationsByUserId(int userId);

    // 게시글 작성자 ID 조회 (댓글 알림에 사용)
    Long selectArticleAuthorId(int articleId);
}