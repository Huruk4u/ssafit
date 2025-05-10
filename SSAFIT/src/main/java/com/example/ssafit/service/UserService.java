package com.example.ssafit.service;

import com.example.ssafit.model.dto.User;

public interface UserService {

    /**
     * 사용자 이름으로 사용자 정보를 조회
     * @param username 사용자의 이름
     * @return User 객체, 사용자 정보
     */
    User findUserByUsername(String username);

    /**
     * 사용자 ID로 사용자 정보를 조회
     * @param userId 사용자 ID
     * @return User 객체, 사용자 정보
     */
    User findUserById(int userId);

    /**
     * 사용자 이름으로 존재 여부 확인
     * @param username 사용자 이름
     * @return 존재하면 true, 없으면 false
     */
    boolean userExists(String username);

    /**
     * 새로운 사용자 추가
     * @param user 추가할 사용자 정보
     */
    void addUser(User user);

    /**
     * 사용자 정보 업데이트
     * @param user 업데이트할 사용자 정보
     */
    void updateUser(User user);

    /**
     * 사용자 삭제
     * @param username 삭제할 사용자 이름
     */
    void deleteUser(String username);
}
