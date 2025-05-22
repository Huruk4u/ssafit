package com.example.ssafit.model.service.user;

import com.example.ssafit.model.dto.user.User;

import java.util.List;

public interface FollowService {

    public List<User> searchFolloweeListByUserId(int userId);

    public int addFollow(int followerId, int followeeId);

    public int removeFollow(int followerId, int followeeId);

}
