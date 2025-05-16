package com.example.ssafit.model.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 사용자 회원가입 폼
 * 1. 이메일 형식 여부 체크
 * 2. 모든 값이 입력되도록 조건 형성
 */
public class RegistForm {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    private String checkPassword;

    @NotBlank
    private String nickname;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank
    private String email;

    @NotNull
    private Double weight;

    @NotNull
    private Double height;

    @NotNull
    private Double muscleMass;

    @NotNull
    private Double bodyFat;

    public RegistForm() {}

    public RegistForm(String userName, String password, String checkPassword, String nickname, String email) {
        this.userName = userName;
        this.password = password;
        this.checkPassword = checkPassword;
        this.nickname = nickname;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
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

    public Double getBmi() {
        return (this.weight / Math.pow(this.height / 100, 2));
    }

    @Override
    public String toString() {
        return "RegistForm{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", checkPassword='" + checkPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email +
                '}';
    }
}
