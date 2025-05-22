package com.example.ssafit.controller;

import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.user.FollowService;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api_follow")
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    @GetMapping("/get/follow")
    public ResponseEntity getMyFollowing(Principal principal) {
        User currentUser = userService.searchByUsername(principal.getName());
        List<User> userList = followService.searchFolloweeListByUserId(currentUser.getUserId());

        if (userList == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(userList);
        }

    }

    @PostMapping("/post/follow/userId/{userId}")
    public ResponseEntity addFollow(@PathVariable("userId") int followeeId, Principal principal) {
        User currentUser = userService.searchByUsername(principal.getName());
        int result = followService.addFollow(currentUser.getUserId(), followeeId);

        return new ResponseEntity(result == 1? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/follow/userId/{userId}")
    public ResponseEntity removeFollow(@PathVariable("userId") int followeeId, Principal principal) {
        User currentUser = userService.searchByUsername(principal.getName());
        int result = followService.removeFollow(currentUser.getUserId(), followeeId);

        return new ResponseEntity(result == 1? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }
}
