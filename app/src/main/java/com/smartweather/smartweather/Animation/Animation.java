package com.smartweather.smartweather.Animation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

public class Animation {
   int lastPosition =-1;
    public void recyclerAnimation(Context context, View view, int position){
        while (position>lastPosition){
            android.view.animation.Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            view.setAnimation(anim);
            lastPosition=position;


            Log.d("Position", "recyclerAnimation: "+position);
        }
        Log.d("Last Position","recyclerAnimation: "+lastPosition);
    }
}
