package com.example.ssafit.model.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VideoSummaryService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private String buildPrompt(String ocrText) {
        return String.format("""
            너는 운동 전문가야. 이제 영상에서 추출한 자막 텍스트를 바탕으로 운동에 대해 설명해줘.
            자막 내용:
            ```text
            %s
            ```
            이 자막을 보고 운동이 무엇인지 설명하고, 그 운동을 진행하는 순서를 하나하나 자세히 말해줘.
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
        OkHttpClient client = new OkHttpClient();

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
