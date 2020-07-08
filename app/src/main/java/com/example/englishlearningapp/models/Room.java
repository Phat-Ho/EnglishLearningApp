package com.example.englishlearningapp.models;

public class Room {
    private int id;
    private String name;
    private Integer numOfPlayers;
    private String password;
    private String timer;
    private int playerCount;

    public Room(int id, String name, Integer numOfPlayers, String password, String timer, int playerCount) {
        this.id = id;
        this.name = name;
        this.numOfPlayers = numOfPlayers;
        this.password = password;
        this.timer = timer;
        this.playerCount = playerCount;
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

    public Integer getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(Integer numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numOfPlayers=" + numOfPlayers +
                ", password='" + password + '\'' +
                ", timer='" + timer + '\'' +
                '}';
    }
}
