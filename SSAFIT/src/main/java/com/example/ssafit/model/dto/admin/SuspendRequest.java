package com.example.ssafit.model.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class SuspendRequest {

    @NotNull
    private int userId;

    private String userName;

    @NotNull
    private LocalDateTime durationDays;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(LocalDateTime durationDays) {
        this.durationDays = durationDays;
    }
}
