package com.example.ssafit.controller;

import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.dto.admin.Report;
import com.example.ssafit.model.dto.admin.SuspendRequest;
import com.example.ssafit.model.service.ReportService;
import com.example.ssafit.model.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin의 관리자 기능
 * 1. user ID로 user삭제 -> 완 (이후 변경)
 * 3. user 정지기간 부여 -> 완
 * 4. 모든 report조회 -> 완
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

    @Autowired
    private ReportService reportService;

    // 모든 user 조회하기
    @GetMapping("/get/user")
    public ResponseEntity getAllUser() {
        System.out.println("요청 도착");
        List<User> userList = userService.searchAllUser();

        if (userList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(userList);
    }

    // 조치되지 않은 Report 조회하기
    @GetMapping("/get/report/notHandled")
    public ResponseEntity getNotHandledReports() {
        System.out.println("요청 도착");
        List<Report> reportList = reportService.searchReportNotHandled();
        System.out.println("요청 처리 완료");
        if (reportList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(reportList);
    }

    // 유저 삭제 -> 영구 정지로 변화를 줘야 함.
    @DeleteMapping("/delete/user/userId/{userId}")
    public void deleteUserByUsername(@PathVariable("userId") int userId) {
        userService.removeByUserId(userId);
    }

    // 유저에게 정지 부여, 혹은 신고 조치
    @PutMapping("/suspend/reportId/{reportId}")
    public ResponseEntity suspendUserByUserId(@RequestBody @Valid SuspendRequest requestForm,
                                              @PathVariable("reportId") int reportId) {
        System.out.println();

        int result = userService.suspendUserByUserId(requestForm.getUserId(), requestForm.getDurationDays());
        System.out.println("유저 정지 기간 부여 완료.");
        
        reportService.modifyReportAction(reportId, requestForm.getDurationDays() + "일 정지");
        System.out.println("조치 내용 업데이트");
        
        return new ResponseEntity(result, result == 1 ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    // 신고 내역 조회
    @GetMapping("/get/report")
    public ResponseEntity getAllReports() {

        List<Report> reportList = reportService.searchAllReports();

        if (reportList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(reportList);
    }

    // 특정 유저의 신고 내역 조회
    @GetMapping("/get/report/userId/{userId}")
    public ResponseEntity getReportsByUserId(@PathVariable("userId") int userId) {
        List<Report> reportList = reportService.searchReportByUserId(userId);

        if (reportList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(reportList);
    }

    // Report Id로 report 조회
    @GetMapping("/get/report/reportId/{reportId}")
    public ResponseEntity getReportByReportId(@PathVariable("reportId") int reportId) {
        Report report = reportService.searchReportByReportId(reportId);

        if (report == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(report);
    }

}
