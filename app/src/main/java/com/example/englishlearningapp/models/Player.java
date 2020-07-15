package com.example.englishlearningapp.models;

import java.io.Serializable;

public class Player implements Serializable {
    private int id;
    private String name;
    private boolean isPlay;

    public Player() {
        this.id = 0;
    }

    public Player(int id, String name, boolean isPlay) {
        this.id = id;
        this.name = name;
        this.isPlay = isPlay;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
