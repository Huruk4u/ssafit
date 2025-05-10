package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.User.User;

import java.util.List;

/**
 * User관련 기능 구현
 * 1. userName으로 user를 조회하는 기능
 * 2. userName으로 user의 존재 여부를 확인하는 기능
 * 3. user를 등록하는 기능
 * 4. user를 업데이트하는 기능
 * 5. user를 삭제하는 기능
 */
public interface UserService {

    // 1. userName으로 user를 조회하는 기능
    User searchByUsername(String username);

    // 2. userName으로 user의 존재 여부를 확인하는 기능
    boolean checkExistsByUsername(String username);

    // 3. user를 등록하는 기능
    int addUser(User user);

    // 4. user를 업데이트하는 기능
    int modifyUser(User user);

    // 5. user를 삭제하는 기능
    int removeByUsername(String username);

    // 6. 모든 user를 출력하는 기능
    List<User> searchAllUser();

}