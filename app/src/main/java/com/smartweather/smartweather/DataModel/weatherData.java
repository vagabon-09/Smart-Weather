package com.smartweather.smartweather.DataModel;

public class weatherData {
    String text;
    String icon;
    String temp_c;
    String time;
    String wind_kph;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp_c() {
        return temp_c;
    }

    public void setTemp_c(String temp_c) {
        this.temp_c = temp_c;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWind_kph() {
        return wind_kph;
    }

    public void setWind_kph(String wind_kph) {
        this.wind_kph = wind_kph;
    }



    public weatherData(String text, String icon, String temp_c, String time, String wind_kph) {
        this.text = text;
        this.icon = icon;
        this.temp_c = temp_c;
        this.time = time;
        this.wind_kph = wind_kph;
    }


}
