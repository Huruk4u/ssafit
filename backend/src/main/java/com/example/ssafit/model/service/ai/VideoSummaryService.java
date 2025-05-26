package com.example.ssafit.model.service.ai;

import com.google.api.gax.batching.RequestBuilder;
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


    public String generateSubtitle(String text) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String escapedPrompt = buildPrompt(text);

        String json = String.format("""
                {
                  "model": "gpt-4o",
                  "messages": [
                    {
                      "role": "user",
                      "content": [
                        { "type": "text", "text": %s },
                      ]
                    }
                  ]
                }
        
                """, escapedPrompt);

        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                return responseBody;
            } else {
                throw new IOException("ChatGPT API 호출 실패 : " + response.code());
            }
        }

    }
}
