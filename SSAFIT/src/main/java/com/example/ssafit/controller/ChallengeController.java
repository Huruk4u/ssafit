package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Inbody;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.ChallengeService;
import com.example.ssafit.model.service.InbodyService;
import com.example.ssafit.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

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

        return ResponseEntity.ok("인바디 정보가 성공적으로 업데이트되었고, 챌린지 기록이 생성되었습니다.");
    }

    // 인바디 기반 운동 태그 추천 (상위 3개 부위)
    @GetMapping("/recommend/{loginUserId}")
    public ResponseEntity<?> recommendTags(@PathVariable("loginUserId") int loginUserId, Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user.getUserId() != loginUserId) {
            return ResponseEntity.status(403).body("접근 권한이 없습니다.");
        }

        List<String> tags = inbodyService.recommendTagsByInbody(loginUserId);
        return ResponseEntity.ok(tags);
    }
}
