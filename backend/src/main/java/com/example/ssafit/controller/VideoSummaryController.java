package com.example.ssafit.controller;

import com.example.ssafit.model.service.ai.OcrService;
import com.example.ssafit.model.service.ai.VideoSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.View;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/api_video_summary")
@RestController
public class VideoSummaryController {

    @Autowired
    private VideoSummaryService videoSummaryService;

    @Autowired
    private OcrService ocrService;

    @PostMapping("/post")
    public ResponseEntity videoSummary(@RequestParam String videoUrl) {
        try {
            InputStream audioStream = ocrService.downloadAudio(videoUrl);

            String transcription = ocrService.transcribeAudio(audioStream);

            String summary = videoSummaryService.generateSubtitle(transcription);

            return ResponseEntity.ok(summary);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
