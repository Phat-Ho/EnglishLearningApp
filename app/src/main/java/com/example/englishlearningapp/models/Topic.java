package com.example.englishlearningapp.models;

public class Topic {
    private int topicId;
    private String topicName;
    private String topicNameVie;
    private int active;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicNameVie() {
        return topicNameVie;
    }

    public void setTopicNameVie(String topicNameVie) {
        this.topicNameVie = topicNameVie;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
