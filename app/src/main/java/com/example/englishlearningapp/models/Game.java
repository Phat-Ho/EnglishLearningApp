package com.example.englishlearningapp.models;

public class Game {
    private int id;
    private long gameDate;
    private String roomName;

    public Game(int id, long gameDate, String roomName) {
        this.id = id;
        this.gameDate = gameDate;
        this.roomName = roomName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getGameDate() {
        return gameDate;
    }

    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
