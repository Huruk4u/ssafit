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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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
