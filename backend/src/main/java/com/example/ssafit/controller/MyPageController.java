package com.example.ssafit.controller;

import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.Inbody;
import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.user.ChallengeSummary;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.dto.user.UserProfileResponseForm;
import com.example.ssafit.model.dto.user.UserSummaryResponse;
import com.example.ssafit.model.service.BadgeService;
import com.example.ssafit.model.service.inbody.ChallengeService;
import com.example.ssafit.model.service.board.ArticleService;
import com.example.ssafit.model.service.inbody.InbodyService;
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

    @Autowired
    private InbodyService inbodyService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/summary/userId/{userId}")
    @Operation(summary = "다른 사용자의 요약 정보 조회", description = "현재 연속 스트릭과 최장 스트릭, 스트릭 캘린더 정보를 반환합니다.")
    public ResponseEntity<?> getUserSummary(@PathVariable int userId) {
        // 1) 사용자 정보 조회
        User user = userService.searchByUserId(userId);
        if (user == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);

        // 2) 대표 뱃지 조회
        String username = user.getUserName();
        String nickname = user.getNickname();
        String profileImage = user.getProfileImage();
        String backgroundImage = user.getBackgroundImage();
        Badge represented = badgeService.getRepresentedBadge(userId);

        // 3) 해당 사용자가 작성한 글 목록 조회
        List<Article> articles = articleService.searchArticleListByUserId(userId);

        // 4) 응답 DTO 조립
        UserSummaryResponse response = new UserSummaryResponse(
                userId,
                username,
                nickname,
                profileImage,
                backgroundImage,
                represented,
                articles
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/badges")
    @Operation(summary = "사용자의 보유 배지 조회", description = "현재 사용자가 획득한 모든 배지를 반환합니다.")
    public ResponseEntity<?> getUserBadges(Principal principal) {
        String username = principal.getName();
        User user = userService.searchByUsername(username);

        if (user == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);

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

        if (user == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);

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

        if (user == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);

        int userId = user.getUserId();

        // 스트릭 정보
        ChallengeSummary summary = challengeService.getChallengeStreak(userId);

        List<Badge> badges = badgeService.getUserBadges(userId);

        Badge representedBadge = badges.stream()
                .filter(Badge::isRepresented)
                .findFirst()
                .orElse(null);

        List<Inbody> userInbodyData = inbodyService.findInbodyListByUserId(user.getUserId());

        UserProfileResponseForm response = new UserProfileResponseForm(summary, userInbodyData, badges, representedBadge, user.getFirstExercise(), user.getSecondExercise(), user.getThirdExercise());
        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/user_id/{userId}")
    public ResponseEntity getArticleListByUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByUserId(userId);
        if (articleList == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/like/user_id/{userId}")
    public ResponseEntity getArticleListByArticleLikeUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByArticleLikeUserId(userId);
        if (articleList == null) throw new CustomBusinessException(ErrorCode.REPORTEE_NOT_FOUND);
        else return ResponseEntity.ok(articleList);
    }
}