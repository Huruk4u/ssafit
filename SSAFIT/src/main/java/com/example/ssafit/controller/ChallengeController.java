package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.Inbody;
import com.example.ssafit.model.dto.RecommendResult;
import com.example.ssafit.model.dto.user.ChallengeSummary;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.*;
import com.example.ssafit.model.service.inbody.InbodyService;
import com.example.ssafit.model.service.inbody.OcrService;
import com.example.ssafit.model.service.inbody.RecommendService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    private RecommendService recommendService;

    // 인바디 정보 업데이트 및 챌린지 기록 생성
    @PostMapping(value = "/post/challenge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateInbody(
            @RequestParam("file") MultipartFile file, Principal principal) {
        // 유저 정보 출력
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        System.out.println("사고지점 1");

        RecommendResult recommend;
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String ocrText = ocrService.extractTextFromImage(tempFile);
            recommend = recommendService.recommendParts(ocrText, tempFile);

            System.out.println(recommend);

        } catch (IOException e) {
            System.out.println("텍스트 추출 중 오류 발생 : RecommendController.recommend()");
            return ResponseEntity.status(500).body("");
        }
        int loginUserId = user.getUserId();

        System.out.println("사고지점 2");

        // 인바디 데이터 업로드
        Inbody inbodyData = new Inbody();
        inbodyData.setUserId(loginUserId);
        inbodyData.setBmi(recommend.getBmi());
        inbodyData.setWeight(recommend.getWeight());
        inbodyData.setMuscleMass(recommend.getMuscle_mass());
        inbodyData.setBodyFat(recommend.getBody_fat_mass());
        inbodyData.setBodyFatPercentage(recommend.getBody_fat_percentage());

        System.out.println("사고지점 3");

        // inbodyDB에 데이터 업로드
        inbodyService.updateInbodyData(inbodyData);

        System.out.println("사고지점 4");

        // 운동 추천 부위
        List<String> exercisePart = recommend.getRecommended_parts();
        user.setFirstExercise(exercisePart.get(0));
        user.setSecondExercise(exercisePart.get(1));
        user.setThirdExercise(exercisePart.get(2));

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