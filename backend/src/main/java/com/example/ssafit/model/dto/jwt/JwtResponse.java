package com.example.ssafit.model.dto.jwt;

import com.example.ssafit.model.dto.user.User;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String token;
    private final User user;

    public JwtResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String gettoken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}