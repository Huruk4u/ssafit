package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.Inbody;
import com.example.ssafit.model.dto.User.Challenge;
import com.example.ssafit.model.dto.User.ChallengeSummary;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api_challenge")
@CrossOrigin(origins = "*")
public class ChallengeController {

    @Autowired
    private InbodyService inbodyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private OcrService ocrService;

    // 인바디 정보 업데이트 및 챌린지 기록 생성
    @PutMapping("/update/{loginUserId}")
    public ResponseEntity<?> updateInbody(
            @PathVariable("loginUserId") int loginUserId,
            @RequestBody Inbody data,
            Principal principal) {

        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user.getUserId() != loginUserId) {
            return ResponseEntity.status(403).body("접근 권한이 없습니다.");
        }

        data.setUserId(loginUserId);
        inbodyService.updateInbodyData(data);

        // 인바디 업데이트 시 오늘 날짜로 챌린지 기록 생성
        LocalDate today = LocalDate.now();
        challengeService.createChallengeRecord(loginUserId, today);

        // 현재 스트릭 정보 조회
        ChallengeSummary streakInfo = challengeService.getChallengeStreak(loginUserId);

        // 최근 획득한 뱃지 조회
        List<Badge> newBadges = badgeService.getRecentlyAwardedBadges(loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "인바디 정보가 성공적으로 업데이트되었고, 챌린지 기록이 생성되었습니다.");
        response.put("currentStreak", streakInfo.getCurrentStreak());
        response.put("longestStreak", streakInfo.getLongestStreak());
        response.put("newBadges", newBadges);

        return ResponseEntity.ok(response);
    }

    // 인바디 기반 운동 태그 추천 (상위 3개 부위)
    @GetMapping("/recommend/{loginUserId}")
    public ResponseEntity recommendTags(@PathVariable("loginUserId") int loginUserId, Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user.getUserId() != loginUserId) {
            return ResponseEntity.status(403).body("접근 권한이 없습니다.");
        }

        List tags = inbodyService.recommendTagsByInbody(loginUserId);
        return ResponseEntity.ok(tags);
    }
}