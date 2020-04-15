package com.example.englishlearningapp.models;
import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {
    private int id;
    private String word;
    private String description;
    private String pronounce;
    private String html;
    private int remembered;

    public Word() {
    }

    public Word(int id, String word, String description, String pronounce, String html) {
        this.id = id;
        this.word = word;
        this.description = description;
        this.pronounce = pronounce;
        this.html = html;
    }

    protected Word(Parcel in) {
        id = in.readInt();
        word = in.readString();
        description = in.readString();
        pronounce = in.readString();
        html = in.readString();
        remembered = in.readInt();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return word;
    }

    public int getRemembered() {
        return remembered;
    }

    public void setRemembered(int remembered) {
        this.remembered = remembered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(word);
        dest.writeString(description);
        dest.writeString(pronounce);
        dest.writeString(html);
        dest.writeInt(remembered);
    }
}
