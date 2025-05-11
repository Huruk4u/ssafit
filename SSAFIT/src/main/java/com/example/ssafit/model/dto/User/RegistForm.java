package com.example.ssafit.model.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistForm {
    private String userName;
    private String password;
    private String checkPassword;
    private String nickname;
    private String email;

    public RegistForm() {}

    public RegistForm(String userName, String password, String checkPassword, String nickname, String email) {
        this.userName = userName;
        this.password = password;
        this.checkPassword = checkPassword;
        this.nickname = nickname;
        this.email = email;
    }

    @Override
    public String toString() {
        return "RegistForm{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", checkPassword='" + checkPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
