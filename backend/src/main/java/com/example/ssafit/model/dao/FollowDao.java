package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.user.User;

import java.util.List;

/**
 * 1. 팔로우 한 유저리스트 불러오기
 * 2. 팔로우 생성
 * 3. 팔로우 삭제
 */
public interface FollowDao {

    public List<User> selectFolloweeListByUserId(int userId);

    public int insertFollow(int followerId, int followeeId);

    public int deleteFollow(int followerId, int followeeId);

    public int selectCountByFollow(int followerId, int followeeId);
}
