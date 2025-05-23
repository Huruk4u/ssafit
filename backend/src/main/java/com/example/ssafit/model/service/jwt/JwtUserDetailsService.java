package com.example.ssafit.model.service.jwt;

import com.example.ssafit.model.dto.user.CustomUserDetails;
import com.example.ssafit.model.dto.user.User;
import com.example.ssafit.model.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * loadUserByUserName
 * 1. 정지 중인 계정 필터링
 * 2. 인증받지 못한 계정 필터링
 * 3. DB에 존재하는 유저 필터링
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.searchByUsername(username);

        System.out.println(user);

        if (user != null) {
            return new CustomUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
