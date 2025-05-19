package com.example.ssafit.controller;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.board.ArticleService;
import com.example.ssafit.model.dto.article.Article;
import com.example.ssafit.model.dto.SearchCondition;
import com.example.ssafit.model.service.BadgeService;
import com.example.ssafit.model.service.user.UserService;
import com.example.ssafit.util.PageNavigation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @GetMapping("/get")
    public ResponseEntity getAllArticle() {
        List<Article> articleList = articleService.searchAllArticle();
        if (articleList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/article_id/{articleId}")
    public ResponseEntity getArticleByArticleId(@PathVariable("articleId") int articleId) {
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) return ResponseEntity.noContent().build();
        articleService.increaseViewCount(articleId);
        return ResponseEntity.ok(article);
    }

    @GetMapping("/get/user_id/{userId}")
    public ResponseEntity getArticleListByUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByUserId(userId);
        if (articleList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(articleList);
    }

    @GetMapping("/get/like/user_id/{userId}")
    public ResponseEntity getArticleListByArticleLikeUserId(@PathVariable("userId") int userId) {
        List<Article> articleList = articleService.searchArticleListByArticleLikeUserId(userId);
        if (articleList == null) return ResponseEntity.noContent().build();
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
            @RequestBody Article articleData,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 게시글 객체 설정 - 실제 로그인한 사용자 ID로 설정
        Article article = new Article();
        article.setUserId(currentUser.getUserId());
        article.setCategory(articleData.getCategory());
        article.setTitle(articleData.getTitle());
        article.setContent(articleData.getContent());
        article.setTag(articleData.getTag());

        // 필수 필드 검증
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("제목은 필수 항목입니다.");
        }

        List<Badge> userBadgesBefore = badgeService.getUserBadges(currentUser.getUserId());

        int result = articleService.addArticle(article);

        if (result != 1) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
        }

        // 작성자 정보 조회
        User author = userService.searchByUserId(originalArticle.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다르면 수정 불가
        if (!author.getUserName().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }

        // 수정 진행
        int result = articleService.modifyArticle(articleId, article);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/article_id/{articleId}")
    public ResponseEntity<?> removeArticle(@PathVariable("articleId") int articleId, Principal principal) {
        String currentUsername = principal.getName();

        // 게시글 조회
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
        }

        // 작성자 정보 조회
        User author = userService.searchByUserId(article.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다른 경우
        if (!author.getUserName().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        // 삭제 진행
        int result = articleService.removeArticle(articleId);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    // 좋아요 기능 - 로그인 사용자 인증 추가
    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(
            @RequestParam("article_id") int articleId,
            Principal principal) {

        // 현재 로그인한 사용자 정보 확인
        User currentUser = userService.searchByUsername(principal.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 게시글 존재 여부 확인
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 게시글 존재 여부 확인
        Article article = articleService.searchArticleByArticleId(articleId);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        }

        boolean result = articleService.disLikeArticle(articleId, currentUser.getUserId());
        return ResponseEntity.ok(result);
    }
}