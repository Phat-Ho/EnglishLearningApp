package com.example.englishlearningapp.models;

public class Topic {
    private int topicId;
    private String topicName;
    private String topicNameVie;
    private String topicImage;
    private int active;
    private int IdServer;

    public Topic() {
        this.topicId = 0;
    }

    public Topic(int topicId, String topicName, String topicNameVie, int active, int idServer) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicNameVie = topicNameVie;
        this.active = active;
        IdServer = idServer;
    }

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

    public int getIdServer() {
        return IdServer;
    }

    public void setIdServer(int idServer) {
        IdServer = idServer;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", topicName='" + topicName + '\'' +
                ", topicNameVie='" + topicNameVie + '\'' +
                ", topicImage='" + topicImage + '\'' +
                ", active=" + active +
                ", IdServer=" + IdServer +
                '}';
    }
}
