package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.NotificationDao;
import com.example.ssafit.model.dto.Notification;
import com.example.ssafit.model.dto.Report;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public int createCommentNotification(int articleId, int commentId, int commentAuthorId) {
        // 게시글 작성자 ID 조회
        Long articleAuthorId = notificationDao.selectArticleAuthorId(articleId);

        // articleAuthorId가 null인 경우 처리
        if (articleAuthorId == null) {
            return 0;
        }

        // 자신의 게시글에 자신이 댓글을 남긴 경우에는 알림을 생성하지 않음
        if (articleAuthorId == commentAuthorId) {
            return 0;
        }

        // 알림 페이로드 생성 (JSON 형태로 저장)
        Map<String, Object> payload = new HashMap<>();
        payload.put("articleId", articleId);
        payload.put("commentId", commentId);
        payload.put("commentAuthorId", commentAuthorId);

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return 0;
        }

        // 알림 객체 생성
        Notification notification = new Notification();
        notification.setUserId(articleAuthorId); // 게시글 작성자에게 알림
        notification.setType("comment");
        notification.setPayload(payloadJson);
        notification.setIsRead(false); // 읽음 상태 명시적으로 설정

        // 알림 저장
        return notificationDao.insertNotification(notification);
    }

    @Override
    @Transactional
    public int createSuspendNotification(Report report) {

        // 신고당한 유저 조회
        User reportee = userService.searchByUserId(report.getReporteeId());
        if (reportee == null) return 0;

        // 셀프 신고 방지
        if (report.getReporterId() == report.getReporteeId()) return 0;

        // 알림 페이로드 생성 (JSON 형태로 저장)
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", report.getAction());
        payload.put("category", report.getReportCategory());

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return 0;
        }

        // 알림 객체 생성
        Notification notification = new Notification();
        notification.setUserId((long) report.getReporteeId()); // 게시글 작성자에게 알림
        notification.setType("report");
        notification.setPayload(payloadJson);
        notification.setIsRead(false); // 읽음 상태 명시적으로 설정

        // 알림 저장
        return notificationDao.insertNotification(notification);
    }

    @Override
    public int addNotification(Notification notification) {
        return notificationDao.insertNotification(notification);
    }

    @Override
    public List<Notification> getNotificationsByUserId(int userId) {
        return notificationDao.selectNotificationsByUserId(userId);
    }

    @Override
    public int markAsRead(Long notificationId) {
        return notificationDao.markNotificationAsRead(notificationId);
    }

    @Override
    public int markAllAsRead(int userId) {
        return notificationDao.markAllNotificationsAsRead(userId);
    }

    @Override
    public int removeNotification(Long notificationId) {
        return notificationDao.deleteNotification(notificationId);
    }

    @Override
    public int removeAllNotifications(int userId) {
        return notificationDao.deleteAllNotificationsByUserId(userId);
    }
}