package com.example.ssafit.controller;

import com.example.ssafit.model.service.ai.OcrService;
import com.example.ssafit.model.service.ai.VideoSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.View;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RequestMapping("/api_video_summary")
@RestController
public class VideoSummaryController {

    @Autowired
    private VideoSummaryService videoSummaryService;

    @Autowired
    private OcrService ocrService;

    @PostMapping("/post")
    public ResponseEntity<String> videoSummary(@RequestParam String videoUrl) {
        try {
            File subtitleFile = ocrService.downloadSubtitle(videoUrl);
            String plainText = ocrService.parseVttToPlainText(subtitleFile);

            String summary = videoSummaryService.generateSubtitle(plainText);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("요약 실패: " + e.getMessage());
        }
    }

}
