package com.example.ssafit.controller;

import com.example.ssafit.model.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

// 텍스트 추출 서비스
@RestController
@RequestMapping("/api_ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/extract")
    public ResponseEntity extractText(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            System.out.println("사고지점 1");
            file.transferTo(tempFile);
            System.out.println("사고지점 2");

            String result = ocrService.extractTextFromImage(tempFile);
            System.out.println("사고지점 3");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            System.out.println("텍스트 추출 중 오류 발생 : OcrController.extractText()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }
}
