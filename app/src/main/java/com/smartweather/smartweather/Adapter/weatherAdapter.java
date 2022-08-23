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

public class weatherAdapter extends RecyclerView.Adapter<weatherAdapter.MyViewHolder> {
    Context context;
    ArrayList<weatherData> weatherDataArrayList;
    ArrayList<conditonData>conditonDataArrayList;
    Animation anim;
    String cvrTime;
//    add this after creating conditon data

    public weatherAdapter(Context context, ArrayList<weatherData> weatherDataArrayList,  ArrayList<conditonData> conditonDataArrayList) {
        this.context = context;
        this.weatherDataArrayList = weatherDataArrayList;
        this.conditonDataArrayList = conditonDataArrayList;
    }






    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        convertDate(weatherDataArrayList.get(position).getTime());



        holder.v_weatherType.setText(weatherDataArrayList.get(position).getText());
        holder.v_temp.setText(weatherDataArrayList.get(position).getTemp_c()+" °C");
        holder.v_time.setText(cvrTime);
        holder.v_windSpeed.setText(weatherDataArrayList.get(position).getWind_kph()+" km/h");
        holder.v_weatherType.setText(conditonDataArrayList.get(position).getText());
        Picasso.get().load("https:"+conditonDataArrayList.get(position).getIcon()).into(holder.v_weatherIconIv);
        anim = new Animation();
        anim.recyclerAnimation(context.getApplicationContext(),holder.itemView,position);
    }

    private void convertDate(String time) {
        String date_s = time;
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm a");
        cvrTime = dt1.format(date);
    }

    @Override
    public int getItemCount() {
        return weatherDataArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView v_temp,v_weatherType,v_windSpeed,v_time;
        ImageView v_weatherIconIv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            v_temp = itemView.findViewById(R.id.s_temp_id);
            v_weatherIconIv = itemView.findViewById(R.id.s_weatherIcon_id);
            v_weatherType = itemView.findViewById(R.id.s_weatherType_id);
            v_windSpeed = itemView.findViewById(R.id.s_windSpeed_id);
            v_time = itemView.findViewById(R.id.s_time_id);
        }
    }
}
