package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.user.ChallengeSummary;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.BadgeService;
import com.example.ssafit.model.service.ChallengeService;
import com.example.ssafit.model.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/summary/userId/{userId}")
    @Operation(summary = "사용자의 요약 정보 조회", description = "현재 연속 스트릭과 최장 스트릭, 스트릭 캘린더 정보를 반환합니다.")
    public ResponseEntity<?> getUserSummary(@PathVariable("userId") int userId) {
        User user = userService.searchByUserId(userId);
        if (user == null) return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");

        // 현재 연속 스트릭 일수, 최장 스트릭 일수, 스트릭 캘린더 맵 조회
        ChallengeSummary summary = challengeService.getChallengeStreak(userId);

        // 응답 데이터에 userId 추가
        Map<String, Object> response = Map.of(
                "currentStreak", summary.getCurrentStreak(),
                "longestStreak", summary.getLongestStreak(),
                "streakCalendar", summary.getStreakCalendar()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/badges")
    @Operation(summary = "사용자의 보유 배지 조회", description = "현재 사용자가 획득한 모든 배지를 반환합니다.")
    public ResponseEntity<?> getUserBadges(Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        List<Badge> badges = badgeService.getUserBadges(user.getUserId());
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/badges/all")
    @Operation(summary = "시스템의 전체 배지 조회", description = "시스템에 등록된 모든 배지를 반환합니다.")
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @PutMapping("/badges/represent/{badgeId}")
    @Operation(summary = "대표 배지 설정", description = "사용자의 대표 배지를 설정합니다.")
    public ResponseEntity<?> setRepresentedBadge(
            @PathVariable String badgeId,
            Principal principal) {

        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        boolean result = badgeService.setRepresentedBadge(user.getUserId(), badgeId);

        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("success", true);
            response.put("message", "대표 배지가 설정되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "배지를 찾을 수 없거나 설정할 수 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "사용자의 전체 프로필 정보 조회", description = "프로필 정보와 챌린지 스트릭 정보를 함께 반환합니다.")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        int userId = user.getUserId();

        // 스트릭 정보
        ChallengeSummary summary = challengeService.getChallengeStreak(userId);

        List<Badge> badges = badgeService.getUserBadges(userId);

        Badge representedBadge = badges.stream()
                .filter(Badge::isRepresented)
                .findFirst()
                .orElse(null);

        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", userId);
        profile.put("username", user.getUserName());
        profile.put("nickname", user.getNickname());
        profile.put("email", user.getEmail());
        profile.put("profileImage", user.getProfileImage());
        profile.put("backgroundImage", user.getBackgroundImage());
//        profile.put("height", user.getHeight());
//        profile.put("weight", user.getWeight());
        profile.put("firstExercise", user.getFirstExercise());
        profile.put("secondExercise", user.getSecondExercise());
        profile.put("thirdExercise", user.getThirdExercise());
        profile.put("currentStreak", summary.getCurrentStreak());
        profile.put("longestStreak", summary.getLongestStreak());
        profile.put("streakCalendar", summary.getStreakCalendar());
        profile.put("badges", badges);
        profile.put("representedBadge", representedBadge);

        return ResponseEntity.ok(profile);
    }
}