package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Badge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BadgeDao {
    List<Badge> selectAllBadges();
    List<Badge> selectUserBadges(int userId);
    boolean isUserHasBadge(@Param("userId") int userId, @Param("badgeId") String badgeId);
    int insertUserBadge(@Param("userId") int userId, @Param("badgeId") String badgeId);
    int resetUserRepresentedBadges(int userId);
    int updateUserBadgeRepresented(@Param("userId") int userId, @Param("badgeId") String badgeId, @Param("isRepresented") boolean isRepresented);
    List<Badge> selectRecentlyAwardedBadges(@Param("userId") int userId, @Param("minutesAgo") int minutesAgo);
}