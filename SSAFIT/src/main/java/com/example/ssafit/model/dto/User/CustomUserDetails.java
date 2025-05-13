package com.example.ssafit.model.dto.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }
    
    // 계정의 만료 상태를 반환하는 메서드
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정의 정지 여부를 반환하는 메서드
    // 일부 기능만 차단할거라서 이 로직을 interceptor에서 처리해줄거임.
    @Override
    public boolean isAccountNonLocked() {
//        // user가 정지되지 않은 상태일 경우, true를 반환
//        if (user.getSuspendStart() == null || user.getSuspendEnd() == null) return true;
//
//        // user가 정지된 상태일 경우, 현재 시점 반환
//        LocalDateTime now = LocalDateTime.now();
//
//        return now.isBefore(user.getSuspendStart()) || now.isAfter(user.getSuspendEnd());
        return true;
    }

    //
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
