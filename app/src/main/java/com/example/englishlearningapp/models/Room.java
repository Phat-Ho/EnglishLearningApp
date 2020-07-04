package com.example.englishlearningapp.models;

public class Room {
    private String name;
    private Integer numOfPlayers;
    private String password;
    private String timer;

    public Room(String name, Integer numOfPlayers, String password, String timer) {
        this.name = name;
        this.numOfPlayers = numOfPlayers;
        this.password = password;
        this.timer = timer;
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
}
