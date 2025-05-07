package com.example.ssafit.model.service;

import com.example.ssafit.exception.BoardNotFoundException;
import com.example.ssafit.model.dto.Board;

import java.util.List;

public interface BoardService {
    Board createBoard(Board board);
    Board getBoardById(Long boardId) throws BoardNotFoundException;
    List<Board> getAllBoards();
    void deleteBoard(Long boardId) throws BoardNotFoundException;
    Board updateBoard(Board boardDto) throws BoardNotFoundException;
}
