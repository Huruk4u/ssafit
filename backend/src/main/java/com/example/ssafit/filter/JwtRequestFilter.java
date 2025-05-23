package com.example.ssafit.filter;

import java.io.IOException;
import java.util.Arrays;

import com.example.ssafit.model.dto.jwt.ErrorResponse;
import com.example.ssafit.model.dto.jwt.TokenBlacklist;
import com.example.ssafit.util.JwtTokenUtil;
import com.example.ssafit.model.service.jwt.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private PathMatcher pathMatcher;

    // token인증 없이 접근 가능한 URL request
    private static final String[] WHITE_LIST_URL = { "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
            "/api/test/**", "/api_auth/authenticate", "/api_user/post/signup", "/swagger-ui/index.html",
            "/images/**"};


    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                    jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {


        String uri = request.getRequestURI();

        if (Arrays.stream(WHITE_LIST_URL).anyMatch(pattern -> pathMatcher.match(pattern, uri))) {
            filterChain.doFilter(request, response);
            return;
        }

        String rawHeader = request.getHeader("Authorization");
        String token = jwtTokenUtil.extractPureToken(rawHeader);

        if (token == null || tokenBlacklist.isBlacklisted(token)) {
            logger.warn("blacklisted된 JWT토큰으로 접근을 시도했습니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("토큰이 만료되었거나, 로그아웃된 상태입니다.");
            return;
        }

        System.out.println("요청 URI: " + request.getRequestURI());

        String username = null;
        String jwtToken = null;

        String requestHeader = request.getHeader("Authorization");
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the
        // Token
        if (requestHeader != null && rawHeader.startsWith("Bearer ")) {
            jwtToken = rawHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                ErrorResponse error = new ErrorResponse("Token Expired", "JWT Token has expired");

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json;charset=UTF-8");

                new ObjectMapper().writeValue(response.getWriter(), error);
                return;
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security
                // Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);

    }

}