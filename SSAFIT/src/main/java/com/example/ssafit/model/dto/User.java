package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 * 추후 수정 가능성이 보이는 field
 * badgeId, profileImage, backgroundImage -> 필드의 타입이 변화할 것 같음
 * height, weight는 아마 challenge의 가장 마지막 기록을 불러오지 않을까???
 */
@Setter
@Getter
public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private String nickname;
    private String email;
    private String profileImage;
    private String backgroundImage;
    private String badgeId; // -> 추후 수정 가능성
    private int height;
    private int weight;
    private boolean enabled;
    private String createdAt;

    public User() {}

    public User(int userId, String userName, String userPassword, String nickname, String email, String profileImage, String backgroundImage, String badgeId, int height, int weight, boolean enabled, String createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.backgroundImage = backgroundImage;
        this.badgeId = badgeId;
        this.height = height;
        this.weight = weight;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", badgeId='" + badgeId + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", enabled=" + enabled +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
