package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.Badge;

import java.util.List;

public interface BadgeService {

    List<Badge> getAllBadges();

    List<Badge> getUserBadges(int userId);

    boolean awardBadge(int userId, String badgeId);

    boolean hasBadge(int userId, String badgeId);

    boolean setRepresentedBadge(int userId, String badgeId);

    void checkAndAwardArticleBadges(int userId);

    void checkAndAwardStreakBadges(int userId, int currentStreak);

    List<Badge> getRecentlyAwardedBadges(int userId);

    Badge getRepresentedBadge(int userId);
}