package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.BadgeDao;
import com.example.ssafit.model.dao.ArticleDao;
import com.example.ssafit.model.dto.Badge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BadgeServiceImpl implements BadgeService {

    // 게시글 관련 뱃지 상수
    private static final String ARTICLE_POSTER_BADGE_ID = "ARTICLE_POSTER_LV1";
    private static final int ARTICLE_BADGE_THRESHOLD = 3;

    // 챌린지 스트릭 관련 뱃지 상수
    private static final String STREAK_BADGE_3_DAYS = "STREAK_3_DAYS";
    private static final String STREAK_BADGE_7_DAYS = "STREAK_7_DAYS";
    private static final String STREAK_BADGE_30_DAYS = "STREAK_30_DAYS";
    private static final String STREAK_BADGE_100_DAYS = "STREAK_100_DAYS";

    private static final Map<Integer, String> STREAK_BADGE_THRESHOLDS = Map.of(
            3, STREAK_BADGE_3_DAYS,
            7, STREAK_BADGE_7_DAYS,
            30, STREAK_BADGE_30_DAYS,
            100, STREAK_BADGE_100_DAYS
    );

    // 최근 얻은 뱃지 조회 시간 범위 (분)
    private static final int RECENT_BADGE_MINUTES = 5;

    @Autowired
    private BadgeDao badgeDao;

    @Autowired
    private ArticleDao articleDao;

    @Override
    public List<Badge> getAllBadges() {
        return badgeDao.selectAllBadges();
    }

    @Override
    public List<Badge> getUserBadges(int userId) {
        return badgeDao.selectUserBadges(userId);
    }

    @Override
    @Transactional
    public boolean awardBadge(int userId, String badgeId) {
        // Check if user already has the badge
        if (hasBadge(userId, badgeId)) {
            return false;
        }

        // Award the badge
        badgeDao.insertUserBadge(userId, badgeId);
        return true;
    }

    @Override
    public boolean hasBadge(int userId, String badgeId) {
        return badgeDao.isUserHasBadge(userId, badgeId);
    }

    @Override
    @Transactional
    public boolean setRepresentedBadge(int userId, String badgeId) {
        if (!hasBadge(userId, badgeId)) {
            return false;
        }

        badgeDao.resetUserRepresentedBadges(userId);

        badgeDao.updateUserBadgeRepresented(userId, badgeId, true);
        return true;
    }

    @Override
    @Transactional
    public void checkAndAwardArticleBadges(int userId) {
        int articleCount = articleDao.selectArticleListByUserId(userId).size();

        // 글3개
        if (articleCount >= ARTICLE_BADGE_THRESHOLD && !hasBadge(userId, ARTICLE_POSTER_BADGE_ID)) {
            awardBadge(userId, ARTICLE_POSTER_BADGE_ID);
        }

        // 추가예정
    }

    @Override
    public List<Badge> getRecentlyAwardedBadges(int userId) {
        return badgeDao.selectRecentlyAwardedBadges(userId, RECENT_BADGE_MINUTES);
    }

    @Override
    public void checkAndAwardStreakBadges(int userId, int currentStreak) {
        // 각 스트릭 기준에 대해 뱃지 부여 여부 확인
        for (Map.Entry<Integer, String> entry : STREAK_BADGE_THRESHOLDS.entrySet()) {
            int threshold = entry.getKey();
            String badgeId = entry.getValue();

            // 현재 스트릭이 기준을 넘고, 아직 뱃지를 보유하고 있지 않다면 부여
            if (currentStreak >= threshold && !hasBadge(userId, badgeId)) {
                awardBadge(userId, badgeId);
            }
        }
    }

}