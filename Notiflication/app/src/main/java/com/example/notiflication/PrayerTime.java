package com.example.notiflication;

public class PrayerTime {

    int image;
    String name;
    String time;
    boolean next;

    public PrayerTime(){
        
    }

    public PrayerTime(int image, String name, String time, boolean next) {
        this.image = image;
        this.name = name;
        this.time = time;
        this.next = next;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }
}
