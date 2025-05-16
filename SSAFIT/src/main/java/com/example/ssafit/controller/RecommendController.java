package com.example.ssafit.controller;

import com.example.ssafit.model.dto.RecommendResult;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.inbody.OcrService;
import com.example.ssafit.model.service.inbody.RecommendService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
