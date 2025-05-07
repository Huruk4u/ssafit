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
