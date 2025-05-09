package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Implementation of UserDao interface using MyBatis
 */
@Repository
public class UserDaoImpl implements UserDao {

    private static final String NAMESPACE = "com.example.ssafit.mapper.UserMyBatisMapper";

    @Autowired
    private SqlSession sqlSession;

    @Override
    public User findByUsername(String username) {
        return sqlSession.selectOne(NAMESPACE + ".findByUsername", username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return sqlSession.selectOne(NAMESPACE + ".existsByUsername", username);
    }

    @Override
    public void insertUser(User user) {
        sqlSession.insert(NAMESPACE + ".insertUser", user);
    }

    @Override
    public void updateUser(User user) {
        sqlSession.update(NAMESPACE + ".updateUser", user);
    }

    @Override
    public void deleteByUsername(String username) {
        sqlSession.delete(NAMESPACE + ".deleteByUsername", username);
    }
}