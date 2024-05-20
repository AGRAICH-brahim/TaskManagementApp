package com.example.task_management.entity;

public class DataTask {

    private String title;
    private String description;
    private String deadline;
    private String Time;
    private String img;
    private String doc_uri;
    private String key;


    public DataTask(String title, String description, String deadline, String Time, String img) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.Time = Time;
        this.img = img;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDoc_uri() {
        return doc_uri;
    }

    public void setDoc_uri(String doc_uri) {
        this.doc_uri = doc_uri;
    }

    public DataTask(){

    }
}
