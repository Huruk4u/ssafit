package com.example.ssafit.model.dao;

import com.example.ssafit.model.dto.User;

/**
 * Data Access Object interface for User entity
 */
public interface UserDao {
    /**
     * Find a user by username
     * @param username the username to search for
     * @return the user with the given username, or null if not found
     */
    User findByUsername(String username);
    
    /**
     * Check if a user exists by username
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Insert a new user
     * @param user the user to insert
     */
    void insertUser(User user);
    
    /**
     * Update a user
     * @param user the user to update
     */
    void updateUser(User user);
    
    /**
     * Delete a user by username
     * @param username the username of the user to delete
     */
    void deleteByUsername(String username);
}