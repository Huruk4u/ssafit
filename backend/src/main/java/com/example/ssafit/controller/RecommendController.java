package com.example.ssafit.controller;

import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.inbody.RecommendService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api_recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private UserService userService;

    @GetMapping("/recommend/userId/{userId}")
    public ResponseEntity recommendParts(@PathVariable("userId") int userId) throws JSONException {
        User user = userService.searchByUserId(userId);
        String firstExercise = user.getFirstExercise();
        String secondExercise = user.getSecondExercise();
        String thirdExercise = user.getThirdExercise();

        // 이것도 그닥 exception은 아니라서 처리 따로 안해줌.
        if (firstExercise == null || secondExercise == null || thirdExercise == null) {
            System.out.println("추천할 수 있는 운동이 없습니다.");
            return ResponseEntity.noContent().build();
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstExercise", firstExercise);
            jsonObject.put("secondExercise", secondExercise);
            jsonObject.put("thirdExercise", thirdExercise);

            return ResponseEntity.ok().build();
        }
    }
}
