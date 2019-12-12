package com.example.eco_logic;

public class Reporte {
    private String user;
    private String date;
    private String time;
    private String humidity;

    public Reporte(String user, String date, String time, String humidity) {
        this.user = user;
        this.date = date;
        this.time = time;
        this.humidity = humidity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
