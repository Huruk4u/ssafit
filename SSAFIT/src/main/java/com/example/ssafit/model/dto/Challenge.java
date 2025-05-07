package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * //TODO
 * streak map을 어떤 방식으로 표현해야할지..
 * 1. Challenge는 기록된 날짜를 가지고 있어야 하고,
 *    User는 Challenge의 List를 가지고 있어야 한다?
 */
@Getter
@Setter
public class Challenge {
    private int challengeId;
    private int userId;
    private int streakCount;
    private int longestStreakCount;
    private String streakMap;

    public Challenge() {}

    public Challenge(int challengeId, int userId, int streakCount, int longestStreakCount, String streakMap) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.streakCount = streakCount;
        this.longestStreakCount = longestStreakCount;
        this.streakMap = streakMap;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "challengeId=" + challengeId +
                ", userId=" + userId +
                ", streakCount=" + streakCount +
                ", longestStreakCount=" + longestStreakCount +
                ", streakMap='" + streakMap + '\'' +
                '}';
    }
}
