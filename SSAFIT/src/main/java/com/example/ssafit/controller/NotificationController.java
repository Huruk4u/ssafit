package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Notification;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.NotificationService;
import com.example.ssafit.model.service.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api_notification")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    // 사용자의 모든 알림 조회
    @GetMapping("/list")
    public ResponseEntity<?> getNotificationList(Principal principal) {
        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<Notification> notifications = notificationService.getNotificationsByUserId(currentUser.getUserId());
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int result = notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(result, result > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // 모든 알림 읽음 처리
    @PutMapping("/read/all")
    public ResponseEntity<?> markAllAsRead(Principal principal) {
        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int result = notificationService.markAllAsRead(currentUser.getUserId());
        return new ResponseEntity<>(result, result > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // 알림 삭제
    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable("notificationId") Long notificationId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int result = notificationService.removeNotification(notificationId);
        return new ResponseEntity<>(result, result > 0 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    // 모든 알림 삭제
    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAllNotifications(Principal principal) {
        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        int result = notificationService.removeAllNotifications(currentUser.getUserId());
        return new ResponseEntity<>(result, result > 0 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }
}