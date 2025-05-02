package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Board;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation of BoardDao interface using MyBatis
 */
@Repository
public class BoardDaoImpl implements BoardDao {

    private static final String NAMESPACE = "com.example.ssafit.mapper.BoardMyBatisMapper";

    @Autowired
    private SqlSession sqlSession;

    @Override
    public void insertBoard(Board board) {
        sqlSession.insert(NAMESPACE + ".insertBoard", board);
    }

    @Override
    public Board getBoardById(Long boardId) {
        return sqlSession.selectOne(NAMESPACE + ".getBoardById", boardId);
    }

    @Override
    public List<Board> getAllBoards() {
        return sqlSession.selectList(NAMESPACE + ".getAllBoards");
    }

    @Override
    public void updateBoard(Board board) {
        sqlSession.update(NAMESPACE + ".updateBoard", board);
    }

    @Override
    public void deleteBoardById(Long boardId) {
        sqlSession.delete(NAMESPACE + ".deleteBoardById", boardId);
    }

    @Override
    public boolean existsById(Long boardId) {
        return sqlSession.selectOne(NAMESPACE + ".existsById", boardId);
    }
}