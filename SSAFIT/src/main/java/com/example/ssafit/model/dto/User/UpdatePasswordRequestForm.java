package com.example.ssafit.model.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자의 비밀번호를 수정하기 위한 입력 폼.
 */
public class UpdatePasswordRequestForm {

    @NotBlank
    private String currentPassword;

    @NotBlank
//    @Size(min) 이런 어노테이션도 있으니 일단 고려해봄.
    private String newPassword;

    private String checkNewPassword;

    public UpdatePasswordRequestForm() {}

    public UpdatePasswordRequestForm(String currentPassword, String newPassword, String checkNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.checkNewPassword = checkNewPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCheckNewPassword() {
        return checkNewPassword;
    }

    public void setCheckNewPassword(String checkNewPassword) {
        this.checkNewPassword = checkNewPassword;
    }

    @Override
    public String toString() {
        return "UpdatePasswordRequestForm{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", checkNewPassword='" + checkNewPassword + '\'' +
                '}';
    }
}
