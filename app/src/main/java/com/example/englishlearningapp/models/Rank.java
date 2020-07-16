package com.example.englishlearningapp.models;

public class Rank {
    private String playerName;
    private int point;

    public Rank(String playerName, int point) {
        this.playerName = playerName;
        this.point = point;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "playerName='" + playerName + '\'' +
                ", point=" + point +
                '}';
    }
}
