package com.example.ssafit.controller;

import com.example.ssafit.model.dto.User.ChallengeSummary;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.ChallengeService;
import com.example.ssafit.model.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api_mypage")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "MyPage", description = "MyPage API")
public class MyPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeService challengeService;

    @GetMapping("/summary")
    public ResponseEntity<?> getUserSummary(Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        int userId = user.getUserId();

        // 현재 연속 스트릭 일수, 최장 스트릭 일수, 스트릭 캘린더 맵 조회
        ChallengeSummary summary = challengeService.getChallengeStreak(userId);

        // 응답 데이터에 userId 추가
        Map<String, Object> response = Map.of(
                "userId", userId,
                "currentStreak", summary.getCurrentStreak(),
                "longestStreak", summary.getLongestStreak(),
                "streakCalendar", summary.getStreakCalendar()
        );

        return ResponseEntity.ok(response);
    }
}
