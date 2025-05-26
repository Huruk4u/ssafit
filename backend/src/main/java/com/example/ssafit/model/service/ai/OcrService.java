package com.example.ssafit.model.service.ai;

import com.example.ssafit.exception.CustomInbodyException;
import com.example.ssafit.exception.ErrorCode;
import com.google.cloud.speech.v1.*;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
                throw new CustomInbodyException(ErrorCode.OCR_INVALID_IMAGE_FORMAT);
            }
            sb.append(res.getFullTextAnnotation().getText());
        }
        return sb.toString();
    }

    /**
     * url로부터 오디오 입력
     */
    public InputStream downloadAudio(String videoUrl) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Users\\sungm\\Desktop\\final\\yt-dlp.exe", "-f", "bestaudio", "--extract-audio", "--audio-format", "mp3", videoUrl
        );

        System.out.println("사고지점1");

        Process process = processBuilder.start();
        System.out.println("사고지점2");

        return process.getInputStream();
    }

    /**
     * downloadAudio -> InputStream으로부터 OCR처리를 해서 문자열을 추출할거임
     *
     */
    public String transcribeAudio(InputStream inputStream) throws IOException {

        SpeechClient speechClient = SpeechClient.create();

        ByteString byteString = ByteString.readFrom(inputStream);

        RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                .setContent(byteString)
                .build();

        // 음성 인식 설정
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();

        RecognizeRequest request = RecognizeRequest.newBuilder()
                .setConfig(config)
                .setAudio(recognitionAudio)
                .build();

        RecognizeResponse response = speechClient.recognize(request);
        StringBuilder transcription = new StringBuilder();
        response.getResultsList().forEach( result ->
                transcription.append(result.getAlternatives(0).getTranscript()).append("\n"));

        return transcription.toString();
    }
}
