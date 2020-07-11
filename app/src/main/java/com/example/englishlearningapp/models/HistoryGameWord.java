package com.example.englishlearningapp.models;

import java.io.Serializable;

public class HistoryGameWord implements Serializable {
    private int id;
    private int gameId;
    private String word;
    private String player;

    public HistoryGameWord(String word, String player) {
        this.word = word;
        this.player = player;
    }

    public HistoryGameWord(int id, int gameId, String word, String player) {
        this.id = id;
        this.gameId = gameId;
        this.word = word;
        this.player = player;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
