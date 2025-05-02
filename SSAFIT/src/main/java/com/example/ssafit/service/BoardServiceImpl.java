package com.example.ssafit.service;

import com.example.ssafit.model.dao.BoardDao;
import com.example.ssafit.exception.BoardNotFoundException;
import com.example.ssafit.model.dto.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardDao bd;

    @Override
    public Board createBoard(Board board) {
        // Set creation and update timestamps
        LocalDateTime now = LocalDateTime.now();
        board.setCreatedAt(now);
        board.setUpdatedAt(now);

        bd.insertBoard(board);
        return board;
    }

    @Override
    public Board getBoardById(Long boardId) throws BoardNotFoundException {
        Board board = bd.getBoardById(boardId);
        if (board == null) {
            throw new BoardNotFoundException("Board with id - " + boardId + " not found.");
        }
        return board;
    }

    @Override
    public List<Board> getAllBoards() {
        return bd.getAllBoards();
    }

    @Override
    public void deleteBoard(Long boardId) throws BoardNotFoundException {
        if (!bd.existsById(boardId)) {
            throw new BoardNotFoundException("Board with id - " + boardId + " not found.");
        }
        bd.deleteBoardById(boardId);
    }

    @Override
    public Board updateBoard(Board board) throws BoardNotFoundException {
        if (!bd.existsById(board.getId())) {
            throw new BoardNotFoundException("Board with id - " + board.getId() + " not found.");
        }

        // Update the timestamp
        board.setUpdatedAt(LocalDateTime.now());

        bd.updateBoard(board);
        return board;
    }
}