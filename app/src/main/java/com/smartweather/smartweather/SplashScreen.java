package com.smartweather.smartweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private ImageView sun;
    private TextView title;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Removing status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Finding through id
        findById();
        //For animation
        setAnimation();
        //For animation background background
        animateBackground();
        //comment no 2
        //For splashing screen
        splash();


    }

    //For splash screen
    private void splash() {
        int SPLASH_TIME = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent splash = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(splash);
                finish();
            }
        }, SPLASH_TIME);
    }

    //For animating background
    private void animateBackground() {
        int TIME_STAMP = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relativeLayout.setBackgroundResource(R.color.shade_blue);
                //Background animation
                Animation back_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.background_anim);
                relativeLayout.startAnimation(back_anim);
            }
        }, TIME_STAMP);
    }

    //For animaiton
    private void setAnimation() {
        //Flash screen sun rise animation
        Animation sun_rise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sun_rise);
        sun.startAnimation(sun_rise);
        //Name translate animation
        Animation name_fade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_name);
        title.startAnimation(name_fade);

    }

    //For finding view through id
    private void findById() {
        sun = findViewById(R.id.fs_sun_id);
        title = findViewById(R.id.fs_name_id);
        relativeLayout = findViewById(R.id.fs_background_id);
    }
}