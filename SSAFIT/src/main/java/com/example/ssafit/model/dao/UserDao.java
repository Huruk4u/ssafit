package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.User.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User관련 기능 구현
 * 1. userName으로 user를 조회하는 기능
 * 2. userName으로 user의 존재 여부를 확인하는 기능
 * 3. user를 등록하는 기능
 * 4. user를 업데이트하는 기능
 * 5. user를 삭제하는 기능
 */
public interface UserDao {
    // 1. userName으로 user를 조회하는 기능
    User selectByUsername(String username);
    
    // 2. userName으로 user의 존재 여부를 확인하는 기능
    boolean checkExistsByUsername(String username);
    
    // 3. user를 등록하는 기능
    void insertUser(User user);
    
    // 4. user를 업데이트하는 기능
    int updateUserStringInfoByUsername(User user);
    
    // 5. user를 삭제하는 기능
    int deleteByUsername(String username);

    // 6. 모든 user를 조회하는 기능
    List<User> selectAllUsers();

    // 7. User의 프로필사진을 업데이트 하는 기능
    void updateUserProfileImageByUsername(@Param("userName") String userName, @Param("fileName") String fileName);

    // 8. User의 배경사진을 업데이트 하는 기능
    void updateUserBackgroundImageByUsername(@Param("userName") String userName, @Param("fileName") String fileName);
}