package com.example.ssafit.controller;

import com.example.ssafit.exception.BoardNotFoundException;
import com.example.ssafit.model.dto.Board;
import com.example.ssafit.model.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {

    @Autowired
    private BoardService boardService;

    @PostMapping(value = "/board")
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board createdBoard = boardService.createBoard(board);
        return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
    }

    @GetMapping(value = "/board/{boardId}")
    public ResponseEntity<Board> getBoardById(@PathVariable("boardId") Long boardId)
            throws BoardNotFoundException {
        try {
            Board board = boardService.getBoardById(boardId);
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (BoardNotFoundException boardNotFoundException) {
            throw boardNotFoundException;
        }
    }

    @GetMapping(value = "/boards")
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @DeleteMapping(value = "/board/{boardId}")
    public ResponseEntity<HttpStatus> deleteBoard(@PathVariable("boardId") Long boardId)
            throws BoardNotFoundException {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/board")
    public ResponseEntity<Board> updateBoard(@RequestBody Board boardDto)
            throws BoardNotFoundException {
        Board updatedBoard = boardService.updateBoard(boardDto);
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }
}
