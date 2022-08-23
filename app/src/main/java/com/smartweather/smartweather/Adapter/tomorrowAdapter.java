package com.smartweather.smartweather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartweather.smartweather.Animation.Animation;
import com.smartweather.smartweather.DataModel.conditonData;
import com.smartweather.smartweather.DataModel.weatherData;
import com.smartweather.smartweather.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class tomorrowAdapter extends RecyclerView.Adapter<tomorrowAdapter.MyViiewHolder> {
    ArrayList<weatherData>weatherDataArrayList;
    ArrayList<conditonData> conditonDataArrayList;
    Animation anim;
    Context context;
    String f_time;

    public tomorrowAdapter(ArrayList<weatherData> weatherDataArrayList, ArrayList<conditonData> conditonDataArrayList, Context context) {
        this.weatherDataArrayList = weatherDataArrayList;
        this.conditonDataArrayList = conditonDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViiewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view,parent,false);
        return new MyViiewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViiewHolder holder, int position) {
        anim = new Animation();
        formatDate(weatherDataArrayList.get(position).getTime());
        holder.t_date.setText(f_time);
        holder.t_windSpeed.setText(weatherDataArrayList.get(position).getWind_kph()+" km/h");
        holder.t_tempC.setText(weatherDataArrayList.get(position).getTemp_c()+" °C");
        holder.t_weatherType.setText(conditonDataArrayList.get(position).getText());
        anim.recyclerAnimation(context.getApplicationContext(), holder.itemView,position);

        Picasso.get().load("https:"+conditonDataArrayList.get(position).getIcon()).into(holder.t_weatherIcon);
    }


    private void formatDate(String time) {
        String date_s = time;
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm");
        Date date = null;
        try {
            date = dt.parse(date_s);
            SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm a");
            f_time = dt1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherDataArrayList.size();
    }

    public class MyViiewHolder extends RecyclerView.ViewHolder {
        TextView t_weatherType,t_tempC,t_windSpeed,t_date;
        ImageView t_weatherIcon;
        public MyViiewHolder(@NonNull View itemView) {
            super(itemView);
            t_weatherType =itemView.findViewById(R.id.s_weatherType_id);
            t_tempC=itemView.findViewById(R.id.s_temp_id);
            t_windSpeed = itemView.findViewById(R.id.s_windSpeed_id);
            t_date = itemView.findViewById(R.id.s_time_id);
            t_weatherIcon = itemView.findViewById(R.id.s_weatherIcon_id);
        }
    }
}
