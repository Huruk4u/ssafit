package com.example.ssafit.model.service;

import com.example.ssafit.model.dto.User.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    int modifyUserStringInfoByUsername(String userName, User user);

    // 5. user를 삭제하는 기능
    int removeByUsername(String username);

    // 6. 모든 user를 출력하는 기능
    List<User> searchAllUser();

    // 7. User의 프로필이미지를 업데이트 하는 기능
    int modifyUserProfileImageByUserName(String userName, MultipartFile file) throws IOException;

}