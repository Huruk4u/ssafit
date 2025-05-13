package com.example.ssafit.model.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class SuspendRequest {

    @NotNull
    private int userId;

    private String userName;

    @NotNull
    private LocalDateTime suspendStart;

    @NotNull
    private LocalDateTime suspendEnd;

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

    public LocalDateTime getSuspendStart() {
        return suspendStart;
    }

    public void setSuspendStart(LocalDateTime suspendStart) {
        this.suspendStart = suspendStart;
    }

    public LocalDateTime getSuspendEnd() {
        return suspendEnd;
    }

    public void setSuspendEnd(LocalDateTime suspendEnd) {
        this.suspendEnd = suspendEnd;
    }
}
