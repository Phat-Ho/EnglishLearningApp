package com.example.englishlearningapp.models;

public class Subject {
    private String subjectName;
    private String subjectImage;

    public Subject(String subjectName, String subjectImage) {
        this.subjectName = subjectName;
        this.subjectImage = subjectImage;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectImage() {
        return subjectImage;
    }

    public void setSubjectImage(String subjectImage) {
        this.subjectImage = subjectImage;
    }
}
