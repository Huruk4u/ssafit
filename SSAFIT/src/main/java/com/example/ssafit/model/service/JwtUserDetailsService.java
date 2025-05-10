package com.example.ssafit.model.service;

import java.util.ArrayList;

import com.example.ssafit.model.dao.UserDao;
import com.example.ssafit.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.searchByUsername(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getUserName(), user.getUserPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
