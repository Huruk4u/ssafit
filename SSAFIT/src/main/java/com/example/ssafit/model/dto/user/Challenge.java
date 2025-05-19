package com.example.ssafit.model.dto.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Challenge {
    private int challengeId;
    private int userId;
    private LocalDate recordDate;
    private LocalDateTime createdAt;

    public Challenge() {
    }

    public Challenge(int challengeId, int userId, LocalDate recordDate, LocalDateTime createdAt) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.recordDate = recordDate;
        this.createdAt = createdAt;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}