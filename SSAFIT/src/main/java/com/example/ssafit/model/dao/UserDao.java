package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.User;

/**
 * Data Access Object interface for User entity
 */
public interface UserDao {

    /**
     * 사용자명(username)으로 사용자 조회
     * @param username 조회할 사용자명
     * @return 해당 사용자 정보, 없으면 null
     */
    User findByUsername(String username);

    /**
     * 사용자 ID(userId)로 사용자 조회
     * @param userId 조회할 사용자 ID
     * @return 해당 사용자 정보, 없으면 null
     */
    User findById(int userId);

    /**
     * 사용자명으로 사용자 존재 여부 확인
     * @param username 확인할 사용자명
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByUsername(String username);

    /**
     * 새로운 사용자 등록
     * @param user 등록할 사용자 정보
     */
    void insertUser(User user);

    /**
     * 사용자 정보 수정
     * @param user 수정할 사용자 정보
     */
    void updateUser(User user);

    /**
     * 사용자명으로 사용자 삭제
     * @param username 삭제할 사용자명
     */
    void deleteByUsername(String username);
}
