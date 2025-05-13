package com.example.ssafit.controller;

import com.example.ssafit.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/api_admin/delete/user/userId/{userId}")
    public void deleteUserByUsername(@PathVariable("userId") String userId) {
    }
}
