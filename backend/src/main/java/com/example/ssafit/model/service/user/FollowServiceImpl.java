package com.example.ssafit.model.service.user;

import com.example.ssafit.controller.FollowController;
import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.ErrorCode;
import com.example.ssafit.model.dao.FollowDao;
import com.example.ssafit.model.dto.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowDao followDao;

    @Autowired
    private UserService userService;

    @Override
    public List<User> searchFolloweeListByUserId(int userId) {
        return followDao.selectFolloweeListByUserId(userId);
    }

    @Override
    @Transactional
    public int addFollow(int followerId, int followeeId) {
        // 팔로워와 팔로우 당하는 사람이 같은 유저면 0
        if (followerId == followeeId) throw new CustomBusinessException(ErrorCode.SELF_FOLLOWED);

        // 팔로잉 당하는 사람이 존재하지 않으면 0
        User followee = userService.searchByUserId(followeeId);
        if (followee == null) throw new CustomBusinessException(ErrorCode.FOLLOWEE_NOT_FOUND);

        if (searchCountByFollow(followerId, followeeId)) throw new CustomBusinessException(ErrorCode.DUPLICATED_FOLLOW_CREATE);

        followDao.insertFollow(followerId, followeeId);
        return 1;
    }

    @Override
    @Transactional
    public int removeFollow(int followerId, int followeeId) {
        // 팔로워와 팔로우 당하는 사람이 같은 유저면 0
        if (followerId == followeeId) throw new CustomBusinessException(ErrorCode.SELF_FOLLOWED);

        // 팔로잉 당하는 사람이 존재하지 않으면 0
        User followee = userService.searchByUserId(followeeId);
        if (followee == null) throw new CustomBusinessException(ErrorCode.FOLLOWEE_NOT_FOUND);

        if (!searchCountByFollow(followerId, followeeId)) throw new CustomBusinessException(ErrorCode.DUPLICATED_FOLLOW_CREATE);

        followDao.deleteFollow(followerId, followeeId);
        return 1;
    }

    private boolean searchCountByFollow(int followerId, int followeeId) {
        int result = followDao.selectCountByFollow(followerId, followeeId);

        return result == 1;
    }
}
