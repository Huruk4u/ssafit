package com.example.ssafit.controller;

import java.util.Objects;

import com.example.ssafit.model.dto.jwt.TokenBlacklist;
import com.example.ssafit.util.JwtTokenUtil;
import com.example.ssafit.model.dto.jwt.JwtRequest;
import com.example.ssafit.model.dto.jwt.JwtResponse;
import com.example.ssafit.model.service.JwtUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


/**
 * User 동작 관련 Controller
 * 1. 인증 토큰 생성
 * 2. User를 DB에 저장
 * 3. User의 토큰 인증 (로그인)
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/api_auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    /**
     * User 토큰 발급
     * @param authenticationRequest
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    // 토큰 blacklist에 토큰을 추가하는 방식으로 로그아웃 진행
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        tokenBlacklist.add(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}