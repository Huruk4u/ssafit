package com.example.ssafit.interceptor;

import com.example.ssafit.model.dto.user.CustomUserDetails;
import com.example.ssafit.model.dto.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * 정지 중인 유저의 커뮤니티 접근제한 인터셉터
 */
public class SuspensionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();
            LocalDateTime now = LocalDateTime.now();

            if (user.getSuspendStart() != null && user.getSuspendEnd() != null
                    && now.isAfter(user.getSuspendStart()) && now.isBefore(user.getSuspendEnd())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("이 사용자는 정지 상태입니다.");
                return false;
            }
        }
        return true;
    }
}
