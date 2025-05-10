package com.example.ssafit.controller;

import com.example.ssafit.model.dto.User.User;
import com.example.ssafit.model.service.JwtUserDetailsService;
import com.example.ssafit.model.service.UserService;
import com.example.ssafit.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api_user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody User user) {

        System.out.println(user);

        userService.addUser(user);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUserName());
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }

    // 모든 user 조회하기
    @GetMapping("/get")
    public ResponseEntity getAllUser() {
        List<User> userList = userService.searchAllUser();

        if (userList == null) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(userList);
    }
}
