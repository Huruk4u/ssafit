package com.example.ssafit.controller;

import com.example.ssafit.model.dto.RecommendResult;
import com.example.ssafit.model.service.inbody.OcrService;
import com.example.ssafit.model.service.inbody.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api_recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private OcrService ocrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity recommend(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String ocrText = ocrService.extractTextFromImage(tempFile);
            RecommendResult recommend = recommendService.recommendParts(ocrText, tempFile);

            System.out.println(recommend);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            System.out.println("텍스트 추출 중 오류 발생 : RecommendController.recommend()");
            return ResponseEntity.status(500).body("");
        }
    }
}
