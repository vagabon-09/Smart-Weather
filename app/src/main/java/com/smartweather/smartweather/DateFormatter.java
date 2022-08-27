package com.smartweather.smartweather;

import android.annotation.SuppressLint;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {


    public void formatTime(String time, String onFormat, String toFormat, TextView view){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat(onFormat);
        Date date;
        try {
            date = dt.parse(time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat(toFormat);
            assert date != null;
            String over_date = dt1.format(date);
            view.setText(over_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
