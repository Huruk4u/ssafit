package com.example.ssafit.service;

import com.example.ssafit.model.dao.UserDao;
import com.example.ssafit.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public boolean userExists(String username) {
        return userDao.existsByUsername(username);
    }

    @Override
    public void addUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    @Override
    public void deleteUser(String username) {
        userDao.deleteByUsername(username);
    }
}
