package com.example.ssafit.model.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class VideoSummaryService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private String buildPrompt(String ocrText) {
        return String.format("""
        너는 운동 전문가야. 너는 영상 자막을 분석해서 해당 운동의 정보를 사용자에게 제공하는 역할을 맡고 있어.

        다음은 영상에서 추출한 자막 텍스트야:
        ```text
        %s
        ```

        아래 조건 중 하나에 따라 응답해줘. 하나만 선택해서 출력해야 해:

        1. 자막 내용이 충분하고, 운동 이름과 설명이 포함돼 있다면:
        - 어떤 운동인지 명시하고,
        - 그 운동이 어떤 부위나 기능 강화에 효과적인지 간단히 설명한 뒤,
        - 운동을 수행하는 단계를 하나씩 상세히 정리해줘.

        2. 자막 내용이 너무 부족해서 운동을 식별할 수 없는 경우:
        - 아래 문구만 그대로 출력해:
        영상의 오디오 정보가 부족합니다.

        ❗주의사항:
        - 다른 말은 절대 하지 마.
        - 감탄사, 인삿말, 도입 설명 없이 조건에 맞는 내용만 응답해.
        - 예시는 아래 형식을 따라야 해.

        ✅ 출력 예시:
        ```
        운동 이름: 스쿼트
        스쿼트는 하체 근력과 코어 안정성을 강화하는 데 효과적인 운동입니다.

        1. 발을 어깨 너비로 벌리고 선다.
        2. 엉덩이를 뒤로 빼며 무릎을 굽힌다.
        3. 허벅지가 바닥과 평행이 될 때까지 앉는다.
        4. 무릎이 발끝을 넘지 않도록 주의한다.
        5. 엉덩이를 조이며 원래 자세로 돌아온다.
        6. 동작을 원하는 횟수만큼 반복한다.
        ```

        또는

        ```
        영상의 오디오 정보가 부족합니다.
        ```
        """, ocrText);
    }

    private String extractContentOnly(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseJson);

        // choices[0].message.content 경로
        return root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    public String generateSubtitle(String text) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // 연결 시도 타임아웃
                .writeTimeout(30, TimeUnit.SECONDS)     // 요청 보내는 데 걸리는 최대 시간
                .readTimeout(60, TimeUnit.SECONDS)      // 응답 받는 데 걸리는 최대 시간
                .build();

        String escapedPrompt = buildPrompt(text);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", "gpt-4o");

        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode message = objectMapper.createObjectNode();
        message.put("role", "user");
        message.put("content", escapedPrompt);
        messages.add(message);

        root.set("messages", messages);
        String json = objectMapper.writeValueAsString(root);

        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {

            int code = response.code();

            String responseText = response.body() == null ? "" : response.body().string();
            String fullResponse = responseText.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
            String content = extractContentOnly(fullResponse);

            System.out.println("ChatGPT response code: " + code);
            System.out.println("ChatGPT response body: " + responseText);

            if (response.isSuccessful()) {
                return content;
            } else {
                throw new IOException("ChatGPT API 호출 실패 : " + response.code());
            }
        }

    }
}
