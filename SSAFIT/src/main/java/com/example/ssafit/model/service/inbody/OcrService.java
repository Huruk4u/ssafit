package com.example.ssafit.model.service.inbody;

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
                throw new IOException("Error : " + res.getError().getMessage());
            }
            sb.append(res.getFullTextAnnotation().getText());
        }
        return sb.toString();
    }
}
