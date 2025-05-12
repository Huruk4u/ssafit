package com.example.ssafit.model.dto.User;


/**
 * TODO
 * 추후 수정 가능성이 보이는 field
 * badgeId, profileImage, backgroundImage -> 필드의 타입이 변화할 것 같음
 * height, weight는 아마 challenge의 가장 마지막 기록을 불러오지 않을까???
 */
public class User {
    private int userId;
    private String userName;
    private String password;
    private String nickname;
    private String email;
    private String profileImage;
    private String backgroundImage;
    private String badgeId; // -> 추후 수정 가능성
    private boolean enabled;
    private String createdAt;
    private String role; // -> 역할 분리

    public User() {}

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", badgeId='" + badgeId + '\'' +
                ", enabled=" + enabled +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
