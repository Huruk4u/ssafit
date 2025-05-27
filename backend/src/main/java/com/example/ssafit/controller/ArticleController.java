package com.example.ssafit.controller;

import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.CustomUnAuthenticationException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.board.ArticleService;
import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.SearchCondition;
import com.example.ssafit.model.service.BadgeService;
import com.example.ssafit.model.service.user.UserService;
import com.example.ssafit.util.PageNavigation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api_article")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@Tag(name = "Article", description = "Article API")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/get")
    public ResponseEntity getAllArticle() {
        List<Article> articleList = articleService.searchAllArticle();
        if (articleList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/article_id/{articleId}")
    public ResponseEntity getArticleByArticleId(@PathVariable("articleId") int articleId, Principal principal) {

        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);

        // 사용자 viewCnt무한으로 올라가는 거 처리해줌.
        String username = principal.getName();
        String key = "article_viewed:" + username + ":" + articleId;

        if (!redisTemplate.hasKey(key)) {
            articleService.increaseViewCount(articleId);
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(30));
        }

        return ResponseEntity.ok(article);
    }

    @GetMapping("/get/user_id/{userId}")
    public ResponseEntity getArticleListByUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByUserId(userId);
        if (articleList == null) throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/like/user_id/{userId}")
    public ResponseEntity getArticleListByArticleLikeUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByArticleLikeUserId(userId);
        if (articleList == null) throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/category/{category}")
    public ResponseEntity getArticleListByCategory(
            @PathVariable("category") String category,
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String word,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "created_at") String orderBy,
            @RequestParam(required = false, defaultValue = "desc") String orderByDir,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "10") int countPerPage) {

        // SearchCondition 객체 생성 및 파라미터 설정
        SearchCondition condition = new SearchCondition();
        condition.setCategory(category);

        // 검색 조건 설정
        if (key != null && !key.isEmpty()) {
            condition.setKey(key);
            condition.setWord(word);
        }

        // 태그 설정
        if (tag != null && !tag.isEmpty()) {
            condition.setTag(tag);
        }

        // 정렬 설정
        if (orderBy != null && !orderBy.isEmpty()) {
            condition.setOrderBy(orderBy);
            condition.setOrderByDir(orderByDir);
        }

        // 페이지네이션 설정
        condition.setCurrentPage(currentPage);
        condition.setCountPerPage(countPerPage);

        List<Article> articles = articleService.searchArticleListByCondition(condition);
        int totalCount = articleService.getTotalCount(condition);

        // 페이지 계산
        PageNavigation pageNav = new PageNavigation(currentPage, totalCount, countPerPage); // Use the parameter

        Map<String, Object> response = new HashMap<>();
        response.put("articles", articles);
        response.put("totalPages", pageNav.getTotalPageCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post/write")
    public ResponseEntity<?> writeArticle(
            @RequestBody @Valid Article articleData,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }

        // 게시글 객체 설정 - 실제 로그인한 사용자 ID로 설정
        Article article = new Article();
        article.setUserId(currentUser.getUserId());
        article.setCategory(articleData.getCategory());
        article.setTitle(articleData.getTitle());
        article.setContent(articleData.getContent());
        article.setTag(articleData.getTag());
        if (articleData.getUrl() != null) article.setUrl(articleData.getUrl());

        List<Badge> userBadgesBefore = badgeService.getUserBadges(currentUser.getUserId());

        int result = articleService.addArticle(article);
        List<Badge> userBadgesAfter = badgeService.getUserBadges(currentUser.getUserId());

        // 뱃지 새로 얻었는가
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);

        if (userBadgesAfter.size() > userBadgesBefore.size()) {
            Map<String, Badge> badgesBeforeMap = new HashMap<>();
            for (Badge badge : userBadgesBefore) {
                badgesBeforeMap.put(badge.getBadgeId(), badge);
            }

            List<Badge> newBadges = userBadgesAfter.stream()
                    .filter(badge -> !badgesBeforeMap.containsKey(badge.getBadgeId()))
                    .toList();

            response.put("newBadges", newBadges);
            response.put("message", "게시글이 등록되었으며, 새로운 배지를 획득하셨습니다!");
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/put/modify/article_id/{articleId}")
    public ResponseEntity<?> modifyArticle(
            @PathVariable("articleId") int articleId,
            @RequestBody Article article,
            Principal principal) {

        String currentUsername = principal.getName();

        // 기존 게시글 조회
        Article originalArticle = articleService.searchArticleByArticleId(articleId);
        if (originalArticle == null) {
            throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        // 작성자 정보 조회
        User author = userService.searchByUserId(originalArticle.getUserId());
        if (author == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }

        // 로그인 사용자와 작성자가 다르면 수정 불가
        if (!author.getUserName().equals(currentUsername)) {
            throw new CustomBusinessException(ErrorCode.BAD_APPROACH);
        }

        // 수정 진행
        int result = articleService.modifyArticle(articleId, article);
        if (result != 1) throw new CustomBusinessException(ErrorCode.BAD_APPROACH);

        // 성공은 했고, 반환할 포맷은 없을 때, 반환하는 HttpStatus
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/article_id/{articleId}")
    public ResponseEntity<?> removeArticle(@PathVariable("articleId") int articleId, Principal principal) {
        String currentUsername = principal.getName();

        // 게시글 조회
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);

        // 작성자 정보 조회
        User author = userService.searchByUserId(article.getUserId());
        if (author == null) throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);

        // 로그인 사용자와 작성자가 다른 경우
        if (!author.getUserName().equals(currentUsername)) throw new CustomBusinessException(ErrorCode.BAD_APPROACH);

        // 삭제 진행
        int result = articleService.removeArticle(articleId);
        if (result != 1) throw new CustomBusinessException(ErrorCode.ARTICLE_REMOVE_FAILED);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    // 좋아요 기능 - 로그인 사용자 인증 추가
    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(
            @RequestParam("article_id") int articleId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }

        // 게시글 존재 여부 확인
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) {
            throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        boolean result = articleService.likeArticle(articleId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }

    // 싫어요 기능 - 로그인 사용자 인증 추가
    @PostMapping("/disLike")
    public ResponseEntity<?> toggleDislike(
            @RequestParam("article_id") int articleId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            throw new CustomUnAuthenticationException(ErrorCode.USER_NOT_FOUND);
        }

        // 게시글 존재 여부 확인
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) {
            throw new CustomBusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        boolean result = articleService.disLikeArticle(articleId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/like/status")
    public ResponseEntity<?> getLikeStatus(
            @RequestParam("article_id") int articleId,
            Principal principal) {

        return ResponseEntity.ok(articleService.getLikeStatus(articleId, principal.getName()));
    }

}