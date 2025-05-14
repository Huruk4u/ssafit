package com.example.ssafit.model.dto;

import java.time.LocalDateTime;

public class Badge {
    private String badgeId;
    private String name;
    private String iconUrl;
    private String description;
    private LocalDateTime earnedAt;
    private boolean isRepresented;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRepresented() {
        return isRepresented;
    }

    public void setRepresented(boolean represented) {
        isRepresented = represented;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}
