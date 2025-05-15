package com.example.ssafit.model.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class OcrService {

    private final ImageAnnotatorClient vision;

    public OcrService(ImageAnnotatorClient vision) {
        this.vision = vision;
    }

    // image file로부터 문자열 추출.
    public String extractTextFromImage(File imageFile) throws IOException {
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(imageFile));

        System.out.println("사고지점 1 : OcrService.extractTextFromImage");
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

        System.out.println("사고지점 2 : OcrService.extractTextFromImage");
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(Arrays.asList(request));
        List<AnnotateImageResponse> responses = response.getResponsesList();

        System.out.println("사고지점 3 : OcrService.extractTextFromImage");
        StringBuilder sb = new StringBuilder();
        for (AnnotateImageResponse res : responses) {
            System.out.println(res);
            if (res.hasError()) {
                throw new IOException("Error : " + res.getError().getMessage());
            }
            sb.append(res.getFullTextAnnotation().getText());
        }
        return sb.toString();
    }
}
