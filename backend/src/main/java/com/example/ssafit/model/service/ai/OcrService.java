package com.example.ssafit.model.service.ai;

import com.example.ssafit.exception.CustomInbodyException;
import com.example.ssafit.exception.ErrorCode;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Service
public class OcrService {

    private final ImageAnnotatorClient vision;

    public OcrService(ImageAnnotatorClient vision) throws IOException, InterruptedException {
        this.vision = vision;
    }

    // image file로부터 문자열 추출.
    public String extractTextFromImage(File imageFile) throws IOException {
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(imageFile));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(Arrays.asList(request));
        List<AnnotateImageResponse> responses = response.getResponsesList();

        StringBuilder sb = new StringBuilder();
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                throw new CustomInbodyException(ErrorCode.OCR_INVALID_IMAGE_FORMAT);
            }
            sb.append(res.getFullTextAnnotation().getText());
        }
        return sb.toString();
    }

    /**
     * url로부터 오디오 입력
     */
    public File downloadSubtitle(String videoUrl) throws IOException, InterruptedException {
        // 저장할 자막 파일 이름 (yt-dlp가 이 이름 기반으로 생성함)
        File subtitleFile = new File("subtitles.en.vtt");

        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Temp\\tools\\yt-dlp.exe",
                "--write-auto-sub",
                "--sub-lang", "en",
                "--skip-download",
                "-o", "subtitles",
                videoUrl
        );

        pb.inheritIO(); // 콘솔 출력 확인용

        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("yt-dlp 자막 다운로드 실패 (exit code: " + exitCode + ")");
        }

        if (!subtitleFile.exists()) {
            throw new IOException("자막 파일 생성되지 않음: " + subtitleFile.getAbsolutePath());
        }

        return subtitleFile;
    }


    public String parseVttToPlainText(File subtitleFile) throws IOException {
        StringBuilder text = new StringBuilder();
        List<String> lines = Files.readAllLines(subtitleFile.toPath(), StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.trim().isEmpty() || line.matches("^\\d+$") || line.contains("-->")) {
                continue; // 타임코드 or 빈 줄 or 인덱스 제거
            }
            text.append(line.trim()).append(" ");
        }

        return text.toString().trim();
    }

}
