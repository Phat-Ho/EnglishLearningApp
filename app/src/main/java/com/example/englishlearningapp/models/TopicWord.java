package com.example.englishlearningapp.models;

public class TopicWord {
    int id;
    int topicId;
    int wordId;
    int IdServer;

    public TopicWord() {
        this.id = 0;
        this.IdServer = 0;
    }

    public TopicWord(int id, int topicId, int wordId) {
        this.id = id;
        this.topicId = topicId;
        this.wordId = wordId;
    }

    public int getIdServer() {
        return IdServer;
    }

    public void setIdServer(int idServer) {
        IdServer = idServer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
