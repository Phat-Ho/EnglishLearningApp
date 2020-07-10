package com.example.englishlearningapp.models;

import java.io.Serializable;

public class HistoryGameWord implements Serializable {
    private String word;
    private String player;

    public HistoryGameWord(String word, String player) {
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
}
