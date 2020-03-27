package com.example.englishlearningapp.models;

import androidx.annotation.NonNull;

public class Word {
    private int id;
    private String word;
    private String description;
    private String pronounce;
    private String html;

    public String getHtml() {
        return html.toString();
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPronounce() {
        return pronounce.toString();
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public Word() {
    }

    public Word(int id, String word, String description, String pronounce, String html) {
        this.id = id;
        this.word = word;
        this.description = description;
        this.pronounce = pronounce;
        this.html = html;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word.toString();
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDescription() {
        return description.toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return word + "";
    }
}
