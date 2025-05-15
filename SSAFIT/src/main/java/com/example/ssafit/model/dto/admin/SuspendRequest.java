package com.example.ssafit.model.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class SuspendRequest {

    @NotNull
    private int userId;

    private String userName;

    @NotNull
    private int durationDays;

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

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }
}
