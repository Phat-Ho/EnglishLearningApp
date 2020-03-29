package com.example.englishlearningapp.utils;

import android.app.Application;

public class GlobalVariable extends Application {
    private int historyIndex;

    public int getHistoryIndex() {
        return historyIndex;
    }

    public void setHistoryIndex(int historyIndex) {
        this.historyIndex = historyIndex;
    }
}
