package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.ChallengeDao;
import com.example.ssafit.model.dto.User.Challenge;
import com.example.ssafit.model.dto.User.ChallengeSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    private ChallengeDao challengeDAO;

    @Override
    public ChallengeSummary getChallengeStreak(int userId) {
        // 사용자의 모든 챌린지 기록을 가져옵니다
        List<Challenge> challenges = challengeDAO.getChallengesByUserId(userId);

        // 날짜를 기준으로 정렬합니다
        challenges.sort(Comparator.comparing(Challenge::getRecordDate));

        // 1. 현재 스트릭 계산
        int currentStreak = calculateCurrentStreak(challenges);

        // 2. 최장 스트릭 계산
        int longestStreak = calculateLongestStreak(challenges);

        // 3. 스트릭 캘린더 맵 생성 (최근 3개월)
        Map<LocalDate, Boolean> streakCalendar = createStreakCalendar(challenges);

        return new ChallengeSummary(currentStreak, longestStreak, streakCalendar);
    }

    @Override
    public Challenge createChallengeRecord(int userId, LocalDate recordDate) {
        // 이미 해당 날짜에 기록이 있는지 확인
        if (hasRecordForDate(userId, recordDate)) {
            return challengeDAO.getChallengeByUserIdAndDate(userId, recordDate);
        }

        // 새 챌린지 기록 생성
        Challenge challenge = new Challenge();
        challenge.setUserId(userId);
        challenge.setRecordDate(recordDate);
        challengeDAO.insertChallenge(challenge);

        return challenge;
    }

    @Override
    public boolean hasRecordForDate(int userId, LocalDate recordDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("recordDate", recordDate);
        Challenge challenge = challengeDAO.getChallengeByUserIdAndDate(userId, recordDate);
        return challenge != null;
    }

    // 현재 스트릭 계산 (오늘까지 연속적으로 기록된 일수)
    private int calculateCurrentStreak(List<Challenge> challenges) {
        if (challenges.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        int streak = 0;

        // 오늘 기록이 있는지 확인
        boolean hasTodayRecord = challenges.stream()
                .anyMatch(c -> c.getRecordDate().equals(today));

        // 시작일 설정 (오늘 또는 어제)
        LocalDate startDate = hasTodayRecord ? today : today.minusDays(1);

        // 연속된 날짜 확인
        for (LocalDate currentDate = startDate; ; currentDate = currentDate.minusDays(1)) {
            final LocalDate dateToCheck = currentDate; // 람다에서 사용할 final 변수

            boolean hasRecord = challenges.stream()
                    .anyMatch(c -> c.getRecordDate().equals(dateToCheck));

            if (!hasRecord) {
                break;
            }

            streak++;
        }

        return streak;
    }

    // 최장 스트릭 계산
    private int calculateLongestStreak(List<Challenge> challenges) {
        if (challenges.isEmpty()) {
            return 0;
        }

        // 날짜를 Set으로 변환하여 검색 속도 향상
        Set<LocalDate> recordDates = challenges.stream()
                .map(Challenge::getRecordDate)
                .collect(Collectors.toSet());

        int longestStreak = 0;
        int currentStreak = 0;

        // 모든 기록에 대해 최장 스트릭 계산
        for (Challenge challenge : challenges) {
            LocalDate date = challenge.getRecordDate();
            LocalDate prevDate = date.minusDays(1);

            // 이전 날짜에 기록이 있는지 확인
            if (recordDates.contains(prevDate)) {
                currentStreak++; // 연속된 날짜면 현재 스트릭 증가
            } else {
                currentStreak = 1; // 연속이 끊겼으면 1부터 다시 시작
            }

            // 최장 스트릭 업데이트
            longestStreak = Math.max(longestStreak, currentStreak);
        }

        return longestStreak;
    }

    // 스트릭 캘린더 맵 생성 (최근 3개월)
    private Map<LocalDate, Boolean> createStreakCalendar(List<Challenge> challenges) {
        // 최근 3개월 날짜 범위 계산
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        // 모든 날짜를 포함하는 맵 초기화 (기본값: false)
        Map<LocalDate, Boolean> calendar = new HashMap<>();

        // 3개월치 날짜를 모두 맵에 추가 (기본적으로 스트릭 없음 상태)
        LocalDate current = threeMonthsAgo;
        while (!current.isAfter(today)) {
            calendar.put(current, false);
            current = current.plusDays(1);
        }

        // 챌린지 기록이 있는 날짜는 true로 설정
        for (Challenge challenge : challenges) {
            LocalDate recordDate = challenge.getRecordDate();
            if (!recordDate.isBefore(threeMonthsAgo) && !recordDate.isAfter(today)) {
                calendar.put(recordDate, true);
            }
        }

        return calendar;
    }
}