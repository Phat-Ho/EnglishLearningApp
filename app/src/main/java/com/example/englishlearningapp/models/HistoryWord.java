package com.example.englishlearningapp.models;

public class HistoryWord {
    private int id;
    private int userId;
    private int wordId;
    private int searchTime;
    private int remembered;
    private int Synchronized;
    private String linkWeb;
    private int isChange;
    private int idServer;

    public HistoryWord() {
        this.searchTime = 0;
        this.Synchronized = 0;
        this.linkWeb = "";
    }

    public HistoryWord(int id, int userId, int wordId, int remembered, int isChange, int idServer) {
        this.id = id;
        this.userId = userId;
        this.wordId = wordId;
        this.remembered = remembered;
        this.isChange = isChange;
        this.idServer = idServer;
    }

    public HistoryWord(int id, int userId, int wordId, int searchTime, int remembered, int aSynchronized, String linkWeb, int isChange, int idServer) {
        this.id = id;
        this.userId = userId;
        this.wordId = wordId;
        this.searchTime = searchTime;
        this.remembered = remembered;
        this.Synchronized = aSynchronized;
        this.linkWeb = linkWeb;
        this.isChange = isChange;
        this.idServer = idServer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    public int getRemembered() {
        return remembered;
    }

    public void setRemembered(int remembered) {
        this.remembered = remembered;
    }

    public int getSynchronized() {
        return Synchronized;
    }

    public void setSynchronized(int aSynchronized) {
        Synchronized = aSynchronized;
    }

    public String getLinkWeb() {
        return linkWeb;
    }

    public void setLinkWeb(String linkWeb) {
        this.linkWeb = linkWeb;
    }

    public int getIsChange() {
        return isChange;
    }

    public void setIsChange(int isChange) {
        this.isChange = isChange;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }
}
