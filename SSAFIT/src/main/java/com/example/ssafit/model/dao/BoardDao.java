package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.Board;

import java.util.List;

/**
 * Data Access Object interface for Board entity
 */
public interface BoardDao {
    /**
     * Insert a new board
     * @param board the board to insert
     */
    void insertBoard(Board board);
    
    /**
     * Get a board by ID
     * @param boardId the ID of the board to get
     * @return the board with the given ID, or null if not found
     */
    Board getBoardById(Long boardId);
    
    /**
     * Get all boards
     * @return a list of all boards
     */
    List<Board> getAllBoards();
    
    /**
     * Update a board
     * @param board the board to update
     */
    void updateBoard(Board board);
    
    /**
     * Delete a board by ID
     * @param boardId the ID of the board to delete
     */
    void deleteBoardById(Long boardId);
    
    /**
     * Check if a board exists by ID
     * @param boardId the ID of the board to check
     * @return true if the board exists, false otherwise
     */
    boolean existsById(Long boardId);
}