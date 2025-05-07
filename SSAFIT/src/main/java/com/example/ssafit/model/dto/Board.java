package com.example.ssafit.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Board {
    private int boardId;
    private String category;

    public Board() {
    }

    public Board(int boardId, String category) {
        this.boardId = boardId;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Board{" +
                "boardId=" + boardId +
                ", category='" + category + '\'' +
                '}';
    }
}
