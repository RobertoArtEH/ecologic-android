package com.example.eco_logic;

public class Clima {
    private String descripcion;
    private String temp;
    private String date;
    private int img;

    public Clima(String descripcion, String temp, String date) {
        this.descripcion = descripcion;
        this.temp = temp;
        this.date = date;

        setImg(descripcion);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getImg() {
        return img;
    }

    public void setImg(String descripcion) {
        switch (descripcion) {
            case "dust":
                this.img = R.drawable.ic_mist;
                break;
            case "overcast clouds":
                this.img = R.drawable.ic_clouds;
                break;
            case "scattered clouds":
            case "broken clouds":
                this.img = R.drawable.ic_broken_clouds;
                break;
            case "shower rain":
            case "rain":
            case "thunderstorm":
                this.img = R.drawable.ic_rain;
                break;
            case "clear sky":
            case "few clouds":
                this.img = R.drawable.ic_sun;
                break;
            case "snow":
                this.img = R.drawable.ic_snow;
                break;
            case "mist":
            case "haze":
                this.img = R.drawable.ic_mist;
                break;
        }
    }
}
