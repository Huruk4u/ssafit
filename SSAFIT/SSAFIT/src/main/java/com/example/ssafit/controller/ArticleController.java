package com.example.ssafit.controller;

import com.example.ssafit.service.ArticleService;
import com.example.ssafit.model.dto.Article;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api_article")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@Tag(name = "Article", description = "Article API")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

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
        else return ResponseEntity.ok(article);
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

    @GetMapping("/get/category/{boardId}")
    public ResponseEntity getArticleListByBoardId(@PathVariable("boardId") int boardId) {
        List<Article> articleList = articleService.searchArticleListByBoardId(boardId);
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
    public ResponseEntity modifyArticle(@PathVariable("articleId") int articleId, @RequestBody Article article) {
        int result = articleService.modifyArticle(articleId, article);
        return new ResponseEntity(result, result == 1 ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/article_id/{articleId}")
    public ResponseEntity removeArticle(@PathVariable("articleId") int articleId) {
        int result = articleService.removeArticle(articleId);
        return new ResponseEntity(result, result == 1 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

}