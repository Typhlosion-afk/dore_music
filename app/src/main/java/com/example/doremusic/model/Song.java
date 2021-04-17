package com.example.doremusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Serializable {

    private String name;
    private String path;
    private String author;
    private int time;
    private int size;

    public Song(String path, String name, String author, int time, int size){
        this.name = name;
        this.path = path;
        this.author = author;
        this.time = time;
        this.size = size;

    }
    public Song(String path, String name, String author, int time){
        this.name = name;
        this.path = path;
        this.author = author;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
