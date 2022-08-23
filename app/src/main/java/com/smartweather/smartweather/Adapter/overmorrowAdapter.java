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

public class overmorrowAdapter extends RecyclerView.Adapter<overmorrowAdapter.MyViewHolder> {
    ArrayList<weatherData> weatherDataArrayList;
    ArrayList<conditonData> conditonDataArrayList;
    Context context;
    Animation anim;
    String f_time;

    public overmorrowAdapter(ArrayList<weatherData> weatherDataArrayList, ArrayList<conditonData> conditonDataArrayList, Context context) {
        this.weatherDataArrayList = weatherDataArrayList;
        this.conditonDataArrayList = conditonDataArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        timeFormat(weatherDataArrayList.get(position).getTime());
        holder.o_tempC.setText(weatherDataArrayList.get(position).getTemp_c()+" °C");
        holder.o_date.setText(f_time);
        holder.o_windSpeed.setText(weatherDataArrayList.get(position).getWind_kph()+" km/h");
        holder.o_weatherType.setText(conditonDataArrayList.get(position).getText());
        Picasso.get().load("https:"+conditonDataArrayList.get(position).getIcon()).into(holder.o_weatherIcon);

        //Setting Animation
        anim = new Animation();
        anim.recyclerAnimation(context.getApplicationContext(),holder.itemView,position);
    }

    private void timeFormat(String time) {
        String date_s = time;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        Date date = null;
        try {
            date = dt.parse(date_s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dt1 = new SimpleDateFormat("hh:mm a");
        f_time= dt1.format(date);
    }

    @Override
    public int getItemCount() {
        return weatherDataArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView o_weatherType,o_tempC,o_windSpeed,o_date;
        ImageView o_weatherIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            o_weatherType =itemView.findViewById(R.id.s_weatherType_id);
            o_tempC=itemView.findViewById(R.id.s_temp_id);
            o_windSpeed = itemView.findViewById(R.id.s_windSpeed_id);
            o_date = itemView.findViewById(R.id.s_time_id);
            o_weatherIcon = itemView.findViewById(R.id.s_weatherIcon_id);
        }
    }
}
