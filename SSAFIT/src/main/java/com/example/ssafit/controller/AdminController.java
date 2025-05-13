package com.example.ssafit.controller;

import com.example.ssafit.model.dto.admin.SuspendRequest;
import com.example.ssafit.model.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin의 관리자 기능
 * 1. user ID로 user삭제
 * 2. user name으로 user삭제
 * 3. user 정지기간 부여
 * 4. 모든 report조회
 * 5. report삭제
 * 6. userId로 report조회 (userId가 피신고자인 report조회)
 * 7. userId로 report조회 (userId가 신고자인 report조회)
 *
 * // 굳이 구현할 필요가 있을까 싶은 기능
 * 3. user의 모든 작성글 조회
 * 4. user의 특정 작성글 조회
 * 5. user의 모든 댓글 조회
 * 6. user의 특정 댓글 조회
 */
@RestController
@RequestMapping("/api_admin")
@PreAuthorize( "hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    // 유저 삭제
    @DeleteMapping("/api_admin/delete/user/userId/{userId}")
    public void deleteUserByUsername(@PathVariable("userId") int userId) {
        userService.removeByUserId(userId);
    }

    @PutMapping("/suspend")
    public ResponseEntity suspendUserByUserId(@RequestBody @Valid SuspendRequest requestForm) {
        userService.suspendUserByUserId(requestForm.getUserId(), requestForm.getSuspendStart(), requestForm.getSuspendEnd());

    }
}
