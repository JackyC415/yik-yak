package com.cbsjchen0601.models;

public class Comments {
    private String content;
    private String date;

    public Comments() {}

    public Comments(String content, String date) {
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
