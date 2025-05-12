package com.example.ssafit.controller;

import com.example.ssafit.model.dto.User.RegistForm;
import com.example.ssafit.model.dto.User.UpdatePasswordRequestForm;
import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.JwtUserDetailsService;
import com.example.ssafit.model.service.UserService;
import com.example.ssafit.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * User전체 조회,
 * User삭제 기능은 admin만이 접근 가능한 기능으로 구현할 것.
 * 관리자 기능은 나중에 따로 분리할거임.
 */
@RestController
@RequestMapping("/api_user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // user회원가입
    @PostMapping("/post/signup")
    public ResponseEntity signUp(@Valid @RequestBody RegistForm registForm) {
        userService.addUser(registForm);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(registForm.getUserName());
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }

    // userName으로 user조회
    @GetMapping("/get/username/{username}")
    public ResponseEntity getUserByUsername(@PathVariable("username") String username) {
        User user = userService.searchByUsername(username);
        if (user == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(user);
    }

    // 모든 user 조회하기
    @GetMapping("/get")
    public ResponseEntity getAllUser() {
        List<User> userList = userService.searchAllUser();

        if (userList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(userList);
    }

    // username으로 user삭제하기
    @DeleteMapping("/delete/username/{username}")
    public ResponseEntity removeUserByUsername(@PathVariable("username") String username) {
        int result = userService.removeByUsername(username);
        return new ResponseEntity(result, result == 1 ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    // user String(닉네임, email) 정보 업데이트
    @PutMapping("/put/userName/{userName}")
    public ResponseEntity modifyUserStringInfoByUsername(@PathVariable("userName") String userName, @RequestBody User user) {
        System.out.println(userName);
        System.out.println(user);

        int result = userService.modifyUserStringInfoByUsername(userName, user);
        return new ResponseEntity(result, result == 1 ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    // User의 password 업데이트
    @PutMapping("/put/userName/{userName}")
    public ResponseEntity modifyUserPasswordByUsername(@PathVariable("userName") String userName,
                                                       @RequestBody UpdatePasswordRequestForm requestForm) {
        try {
            userService.modifyUserPasswordByUsername(userName, requestForm);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // User의 프로필 이미지를 업데이트 하기
    @PutMapping("/put/profileImage")
    public ResponseEntity modifyProfileImageByUsername(@RequestParam("userName") String userName, @RequestParam("file") MultipartFile file) {
        try {
            userService.modifyUserProfileImageByUserName(userName, file);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    // User의 배경 이미지를 업데이트하기
    @PutMapping("/put/backgroundImage")
    public ResponseEntity modifyBackgroundImageByUsername(@RequestParam("userName") String userName, @RequestParam("file") MultipartFile file) {
        try {
            userService.modifyUserBackgroundImageByUserName(userName, file);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
