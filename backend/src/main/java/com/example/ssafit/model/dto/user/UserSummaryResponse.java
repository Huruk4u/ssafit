package com.example.ssafit.model.dto.user;

import com.example.ssafit.model.dto.Badge;
import com.example.ssafit.model.dto.article.Article;
import java.util.List;

public class UserSummaryResponse {
    private int userId;
    private String userName;
    private String nickname;
    private String profileImage;
    private String backgroundImage;
    private Badge representedBadge;
    private List<Article> articles;

    public UserSummaryResponse(int userId, String userName, String nickname,
                               String profileImage, String backgroundImage,
                               Badge representedBadge, List<Article> articles) {
        this.userId = userId;
        this.userName = userName;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.backgroundImage = backgroundImage;
        this.representedBadge = representedBadge;
        this.articles = articles;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Badge getRepresentedBadge() {
        return representedBadge;
    }

    public void setRepresentedBadge(Badge representedBadge) {
        this.representedBadge = representedBadge;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}