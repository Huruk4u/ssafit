package com.example.ssafit.model.service.inbody;

import com.example.ssafit.model.dto.RecommendResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@Service
public class RecommendService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    /**
     * 1. ocrService로부터 ocrText를 입력받고,
     * 2. callChatGPT메서드로 사진 + ocr파싱된 문자열 전송
     * 3. json타입 데이터를 반환받고 출력
     */
    public RecommendResult recommendParts(String ocrText, File imageFile) throws IOException {
        String prompt = buildPrompt(ocrText);
        ;
        String convertedImageUrl = convertToBase64ImageUrl(imageFile);
        String responseJson = callChatGPT(prompt, convertedImageUrl);

        System.out.println(responseJson);

        return parseResponse(responseJson);
    }

    // ocrText를 입력받아 gpt 입력 프롬프트 빌드
    private String buildPrompt(String ocrText) {
        return String.format("""
                너는 인바디 결과를 분석해 운동이 필요한 신체 부위 3가지를 추천하는 전문가야.
                다음은 OCR로 추출한 인바디 결과의 텍스트야:
                ```text
                %s
                ```
                이 데이터를 기반으로 아래 정보를 추출해서 JSON 형태로 응답해줘:
                        1. 운동이 필요한 신체 부위 3가지를 추천 → 배열 형태로 출력
                        2. 다음 인바디 주요 수치 5가지를 함께 추출:
                          - 체중 (weight)
                          - 골격근량 (muscle_mass)
                          - 체지방량 (body_fat_mass)
                          - BMI (bmi)
                          - 체지방률 (body_fat_percentage)
                        3. 아래 파일은 응답 예시야. 아래 형식처럼 JSON 객체 하나만 정확히 반환해줘:
                        {
                        "recommended_parts":["복부", "하체", "등"],
                        "weight": 79.9,
                        "muscle_mass": 25.4,
                        "body_fat_mass": 34.1,
                        "bmi": 31.5,
                        "body_fat_percentage": 42.7
                        }
                다른 말은 절대 하지 말고, 위 예시의 형식을 정확히 지켜서 JSON 객체 하나만 정확히 응답해.
                """, ocrText
        );
    }

    // 입력받은 이미지는 data:byte[]로 변환 후, gpt에 적합한 형식으로 변환한다.
    public String convertToBase64ImageUrl(File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath()) == null ? "image/jpeg" : Files.probeContentType(file.toPath());
        System.out.println("convertToBase64ImageUrl : 사고지점 1");
        byte[] bytes = Files.readAllBytes(file.toPath());
        System.out.println("convertToBase64ImageUrl : 사고지점 2");
        String base64 = Base64.getEncoder().encodeToString(bytes);
        System.out.println("convertToBase64ImageUrl : 사고지점 3");
        return "data:" + mimeType + ";base64," + base64;
    }

    public String callChatGPT(String prompt, String imageDataUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        System.out.println("callChatGPT : 사고지점 1");

        MediaType mediaType = MediaType.parse("application/json");

        System.out.println("callChatGPT : 사고지점 2");

        // GPT 요청 JSON 구성
        ObjectMapper objectMapper = new ObjectMapper();
        String escapedPrompt = objectMapper.writeValueAsString(prompt);
        String jsonBody = String.format("""
                {
                  "model": "gpt-4o",
                  "messages": [
                    {
                      "role": "user",
                      "content": [
                        { "type": "text", "text": %s },
                        { "type": "image_url", "image_url": { "url": "%s" } }
                      ]
                    }
                  ]
                }
                """, escapedPrompt, imageDataUrl);


        System.out.println("callChatGPT : 사고지점 3");
        System.out.println(jsonBody);
        // chatGPT에 전송할 request생성하고,
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(jsonBody, mediaType))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        System.out.println("callChatGPT : 사고지점 4");

        // execute 후, 여기서 응답 받아오는 거임.
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() == null ? "" : response.body().string();
                System.out.println("GPI API 호출 실패");
                System.out.println("응답 코드: " + response.code());
                System.out.println("응답 메시지: " + response.message());
                System.out.println("응답 본문: " + errorBody);
                throw new IOException("GPT API 호출 실패" + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    public RecommendResult parseResponse(String responseJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = objectMapper.readTree(responseJson);

        // chatGPT가 반환한 response의 핵심 content
        String content = root.path("choices").path(0).path("message").path("content").asText();

        // 마크다운 제거
        String cleaned = content
                .replaceAll("(?s)^```json\\s*", "") // 시작 부분의 ```json 제거
                .replaceAll("(?s)```\\s*$", "")     // 끝 부분의 ``` 제거
                .trim(); // 앞뒤 공백 제거

        System.out.println("chatGPT response content: " + cleaned);

        return objectMapper.readValue(cleaned, RecommendResult.class);
    }

}
