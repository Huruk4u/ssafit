package com.example.ssafit.model.dto.user;

import java.time.LocalDate;
import java.util.Map;

public class ChallengeSummary {
    private int currentStreak;
    private int longestStreak;
    private Map<String, Boolean> streakCalendar;

    public ChallengeSummary() {
    }

    public ChallengeSummary(int currentStreak, int longestStreak, Map<String, Boolean> streakCalendar) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.streakCalendar = streakCalendar;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public Map<String, Boolean> getStreakCalendar() {
        return streakCalendar;
    }

    public void setStreakCalendar(Map<String, Boolean> streakCalendar) {
        this.streakCalendar = streakCalendar;
    }
}