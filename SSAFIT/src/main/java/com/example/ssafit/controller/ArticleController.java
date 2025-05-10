package com.example.ssafit.controller;

import com.example.ssafit.model.dto.User;
import com.example.ssafit.service.ArticleService;
import com.example.ssafit.model.dto.Article;
import com.example.ssafit.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity getArticleListByBoardId(@PathVariable("category") int category) {
        List<Article> articleList = articleService.searchArticleListByBoardId(category);
        if (articleList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(articleList);
    }

    // 6. searchForm으로 글들을 조회. -> 미구현

    @PostMapping("/post/write")
    public ResponseEntity writeArticle(@RequestBody Article article) {
        int result = articleService.addArticle(article);
        return new ResponseEntity(result, result == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
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
        User author = userService.findUserById(originalArticle.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다르면 수정 불가
        if (!author.getUsername().equals(currentUsername)) {
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
        User author = userService.findUserById(article.getUserId());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성자 정보를 찾을 수 없습니다.");
        }

        // 로그인 사용자와 작성자가 다른 경우
        if (!author.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        // 삭제 진행
        int result = articleService.removeArticle(articleId);
        return new ResponseEntity<>(result, result == 1 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/like")
    public ResponseEntity<?> toggleLike(@RequestParam("article_id") int articleId,
                                        @RequestParam("userId") int userId) {
        boolean result = articleService.likeArticle(articleId, userId);
        return new ResponseEntity<>(result, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/disLike")
    public ResponseEntity<?> toggleDislike(@RequestParam("article_id") int articleId,
                                           @RequestParam("userId") int userId) {
        boolean result = articleService.disLikeArticle(articleId, userId);
        return new ResponseEntity<>(result, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }



}