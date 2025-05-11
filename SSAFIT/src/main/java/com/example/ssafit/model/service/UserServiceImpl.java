package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.UserDao;
import com.example.ssafit.model.dto.User.RegistForm;
import com.example.ssafit.model.dto.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    private static final String UPLOAD_PROFILE_IMAGE_PATH = "C:\\Temp\\profile";

    private static final String UPLOAD_BACKGROUND_IMAGE_PATH = "C:\\Temp\\background";

    @Override
    public User searchByUsername(String username) {
        User user = userDao.selectByUsername(username);
        return user;
    }

    @Override
    public boolean checkExistsByUsername(String username) {
        return userDao.checkExistsByUsername(username);
    }

    // User 등록
    @Override
    public int addUser(RegistForm registForm) {
        // registForm의 비밀번호 확인 여부 체크
        if (!registForm.getPassword().equals(registForm.getCheckPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // registForm의 유저아이디 중복 여부 체크
        if (checkExistsByUsername(registForm.getUserName())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        if (registForm.getUserName() == null || registForm.getPassword() == null
                || registForm.getNickname() == null || registForm.getEmail() == null) {
            throw new RuntimeException("모든 정보를 입력해야 합니다.");
        }

        // User등록을 위한 입력 폼 정보
        User newUser = new User();
        newUser.setUserName(registForm.getUserName());
        newUser.setPassword(passwordEncoder.encode(registForm.getPassword()));
        newUser.setNickname(registForm.getNickname());
        newUser.setEmail(registForm.getEmail());

        userDao.insertUser(newUser);
        return 1;
    }

    // User의 이메일, 닉네임 정보를 변경.
    @Override
    public int modifyUserStringInfoByUsername(String userName, User user) {
        user.setUserName(userName);
        System.out.println(user);
        userDao.updateUserStringInfoByUsername(user);
        return 1;
    }

    @Override
    public int removeByUsername(String username) {
        userDao.deleteByUsername(username);
        return 1;
    }

    @Override
    public List<User> searchAllUser() {
        return userDao.selectAllUsers();
    }

    // User의 프로필 이미지를 업로드.
    @Override
    public void modifyUserProfileImageByUserName(String userName, MultipartFile file) throws IOException {
        // original File name
        String originalFileName = file.getOriginalFilename();

        // original FileName으로부터 extension분리
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        System.out.println(String.format("저장한 파일명 : %s", fileName));

        File target = new File(UPLOAD_PROFILE_IMAGE_PATH, fileName);
        file.transferTo(target);

        userDao.updateUserProfileImageByUsername(userName, fileName);
    }

    // User의 배경 사진을 업로드.
    @Override
    public void modifyUserBackgroundImageByUserName(String userName, MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        File target = new File(UPLOAD_BACKGROUND_IMAGE_PATH, fileName);
        file.transferTo(target);

        userDao.updateUserBackgroundImageByUsername(userName, fileName);
    }
}
