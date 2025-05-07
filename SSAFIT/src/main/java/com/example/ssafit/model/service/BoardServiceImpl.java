//package com.example.ssafit.model.service;
//
//import com.example.ssafit.model.dao.BoardDao;
//import com.example.ssafit.exception.BoardNotFoundException;
//import com.example.ssafit.model.dto.Board;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class BoardServiceImpl implements BoardService {
//
//    @Autowired
//    private BoardDao boardDao;
//
//    @Override
//    public Board createBoard(Board board) {
//        // Set creation and update timestamps
//        LocalDateTime now = LocalDateTime.now();
//
//        boardDao.insertBoard(board);
//        return board;
//    }
//
//    @Override
//    public Board getBoardById(Long boardId) throws BoardNotFoundException {
//        Board board = boardDao.getBoardById(boardId);
//        if (board == null) {
//            throw new BoardNotFoundException("Board with id - " + boardId + " not found.");
//        }
//        return board;
//    }
//
//    @Override
//    public List<Board> getAllBoards() {
//        return boardDao.getAllBoards();
//    }
//
//    @Override
//    public void deleteBoard(Long boardId) throws BoardNotFoundException {
//        if (!boardDao.existsById(boardId)) {
//            throw new BoardNotFoundException("Board with id - " + boardId + " not found.");
//        }
//        boardDao.deleteBoardById(boardId);
//    }
//
//    @Override
//    public Board updateBoard(Board board) throws BoardNotFoundException {
//        if (!boardDao.existsById(board.getId())) {
//            throw new BoardNotFoundException("Board with id - " + board.getId() + " not found.");
//        }
//
//        // Update the timestamp
//        boardDao.updateBoard(board);
//        return board;
//    }
//}