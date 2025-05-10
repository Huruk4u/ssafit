package com.example.ssafit.model.service;

import com.example.ssafit.model.dao.UserDao;
import com.example.ssafit.model.dto.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Override
    public User searchByUsername(String username) {
        User user = userDao.selectByUsername(username);
        System.out.println(user);
        return user;
    }

    @Override
    public boolean checkExistsByUsername(String username) {
        return userDao.checkExistsByUsername(username);
    }

    @Override
    public int addUser(User user) {
        if (checkExistsByUsername(user.getUserName())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setNickname(user.getNickname());
        newUser.setEmail(user.getEmail());

        userDao.insertUser(newUser);
        return 1;
    }

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

    @Override
    public int modifyUserProfileImageByUserName(String userName, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

        String fileName = UUID.randomUUID().toString() + "." + extension;

        String uploadDir = "C:/Users/sungm/Desktop/Spring/ssafit/SSAFIT/src/main/resources/static/images/userProfileImage/";
        File saveFile = new File(uploadDir + fileName);
        file.transferTo(saveFile);

        userDao.updateUserProfileImageByUsername(userName, fileName);

        return 1;
    }
}
