package com.smartweather.smartweather;

import static com.smartweather.smartweather.R.layout.popup_card_weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.smartweather.smartweather.Adapter.overmorrowAdapter;
import com.smartweather.smartweather.Adapter.tomorrowAdapter;
import com.smartweather.smartweather.Adapter.weatherAdapter;
import com.smartweather.smartweather.DataModel.conditonData;
import com.smartweather.smartweather.DataModel.weatherData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tempTv, windTv, localityTv, weatherTypeTv, weatherDateTv, Title_todayDate, Title_tomorrowDate, Title_overmDate, pop_temp_c, pop_temp_f, pop_cloud, pop_wind_speed, pop_uv, pop_sun_rise, pop_sun_set, pop_moon_rise, pop_moon_set;
    private CardView card_view_btn;
    private ImageView weatherIconImgv, searchBtn;
    private String search;
    int REQUEST_CODE = 101;
    LocationManager locationManager;
    List<Address> addressList;
    Location location;
    private ArrayList<weatherData> weatherDataList;
    private ArrayList<weatherData> weatherDataArrayList1;
    private ArrayList<weatherData> weatherDataArrayList2;
    private ArrayList<conditonData> conditonDataArrayList;
    private RecyclerView recyclerView, tmr_rv, ovrm_rv;
    Gson gson = new Gson();
    String city = "";
    private JSONObject result, condition;
    private JSONObject current;
    private int temp;
    private int tempF;
    private int windSpeed;
    private String text;
    private JSONObject today;
    private ShimmerFrameLayout card_fb_shimmer;
    private LinearLayout card_details, rv_parent_tday, tmrw_parent, over_shimmem_layout;
    private SwipeRefreshLayout mRefreshing;
    String base_url;
    DateFormatter dateFormatter;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Finding View
        findById();
        //hide card details
        card_details.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tmr_rv.setVisibility(View.GONE);
        dateFormatter = new DateFormatter();
        //Start Shimmer
        card_fb_shimmer.startShimmer();
//        rv_parent_tday.startShimmer();


        //Recycler View
        searchBtn();
        //Get search value
        getSearchValue();
        //get weather forecast data according to the search data
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mRefreshing = findViewById(R.id.refresh_id);
        mRefreshing.setOnRefreshListener(() -> {
            mRefreshing.setRefreshing(false);
            base_url = givePermissions();
            getData(base_url);
        });

        if (search != null) {
            base_url = "https://api.weatherapi.com/v1/forecast.json?key=ed7111cc88ee4769858141158222207&q=" + search + "&days=10&aqi=yes&alerts=yes";
        } else {
            base_url = givePermissions();
        }
        getData(base_url);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        tmr_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        ovrm_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));


        /*
         If some one click first card of the view then a popup window will show where some data will placed and before show
        popup window a Interstitial Ad will show
         */
        // Popup button
        card_view_btn.setOnClickListener(view -> {
            if (result != null) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                showPopUp();
            } else {
                Toast.makeText(MainActivity.this, "Wait until data loaded..", Toast.LENGTH_SHORT).show();
            }

        });
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.recycler_view_card_shimmen, null);
        ShimmerFrameLayout sfl = view.findViewById(R.id.shimmer_rv_card_id);
        sfl.stopShimmer();
        admobAds();
    }

    private void admobAds() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        bannerAdd();
        interstitialAd();
    }

    private void interstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-6126904020434443/2150604708", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d("TAG", "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d("TAG", "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e("TAG", "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d("TAG", "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d("TAG", "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                        super.onAdFailedToLoad(loadAdError);
                        interstitialAd();
                    }
                });



    }

    private void bannerAdd() {
        mAdView = findViewById(R.id.bannerAdd1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }

    @SuppressLint("ResourceType")
    private void showPopUp() {
        AlertDialog.Builder dilogBuilder = new AlertDialog.Builder(this);

        final View view = getLayoutInflater().inflate(popup_card_weather, null);

        //Finding through id
        pop_temp_c = view.findViewById(R.id.pup_temp_c_id);
        pop_temp_f = view.findViewById(R.id.pup_temp_f_id);
        pop_cloud = view.findViewById(R.id.pup_weather_type_id);
        pop_wind_speed = view.findViewById(R.id.pup_wind_speed_id);
        pop_uv = view.findViewById(R.id.pup_uv_id);
        pop_moon_rise = view.findViewById(R.id.pup_moon_rise_id);
        pop_moon_set = view.findViewById(R.id.pup_moon_set_id);
        pop_sun_rise = view.findViewById(R.id.pup_sun_rise_id);
        pop_sun_set = view.findViewById(R.id.pup_sun_set_id);

        //Setting data to pop up window
        setToPopup();

        dilogBuilder.setView(view);
        AlertDialog dialog = dilogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.slide_in_left));
        dialog.show();
    }


    //Asking users for Location Access..

    private String givePermissions() {
        boolean gpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsEnable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                allPermission();
            } else {

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                getCity();
            }

        } else {
            gpsDialog();
        }

        return "https://api.weatherapi.com/v1/forecast.json?key=ed7111cc88ee4769858141158222207&q=" + city + "&days=10&aqi=yes&alerts=yes";

    }

    private void gpsDialog() {
        AlertDialog.Builder Adialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gps_alert, null);
        Adialog.setView(view);
        AlertDialog dialog = Adialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void getCity() {
        if (location != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                city = addressList.get(0).getLocality();
                Log.d("Locality ", "getLocation: " + city);
                Log.d("AddressList ", "getLocation: " + addressList.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private void allPermission() {
        new Handler().postDelayed(this::givePermissions, 3000);
    }


    //Getting search value form search bar
    private void getSearchValue() {
        Intent intent = getIntent();
        search = intent.getStringExtra("rj");
        Log.d("Search_m", "getSearchValue: " + search);
    }

    //Search button
    private void searchBtn() {
        searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Search.class);
            startActivity(intent);
        });

    }

    //Getting data from API
    private void getData(String base_url) {

        AndroidNetworking.get(base_url).setPriority(Priority.HIGH).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                weatherDataList = new ArrayList<>();
                conditonDataArrayList = new ArrayList<>();
                weatherDataArrayList1 = new ArrayList<>();
                weatherDataArrayList2 = new ArrayList<>();

                if (response != null) {
                    card_fb_shimmer.stopShimmer();
                    card_fb_shimmer.setVisibility(View.GONE);
                    card_details.setVisibility(View.VISIBLE);
                    rv_parent_tday.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    tmrw_parent.setVisibility(View.GONE);
                    tmr_rv.setVisibility(View.VISIBLE);
                    over_shimmem_layout.setVisibility(View.GONE);
                    ovrm_rv.setVisibility(View.VISIBLE);

                    try {
                        result = response.getJSONObject("location");
                        String city_name = result.getString("name");
                        String localtime = result.getString("localtime");


                        current = response.getJSONObject("current");
                        condition = current.getJSONObject("condition");
                        temp = current.getInt("temp_c");
                        tempF = current.getInt("temp_f");
                        windSpeed = current.getInt("wind_kph");
                        text = condition.getString("text");
                        String url = condition.getString("icon");

                        JSONObject forecast = response.getJSONObject("forecast");
                        JSONArray forecastday = forecast.getJSONArray("forecastday");
                        today = forecastday.getJSONObject(0);
                        JSONArray tdy_hour = today.getJSONArray("hour");


                        JSONObject tomorrow = forecastday.getJSONObject(1);
                        JSONArray tomorrow_hour = tomorrow.getJSONArray("hour");
                        String tmrw_date = tomorrow.getString("date");
                        setTmrDate(tmrw_date);

                        JSONObject overmorrow = forecastday.getJSONObject(2);
                        JSONArray overmorrow_hour = overmorrow.getJSONArray("hour");
                        String ovrm_date = overmorrow.getString("date");
                        setOvrmDate(ovrm_date);


                        String today_date = today.getString("date");


                        //Today add data
                        todaySetData(tdy_hour);
                        //Setting tomorrow data
                        tomorrowData(tomorrow_hour);
                        //Setting Overmorrow data
                        overmorrowData(overmorrow_hour);
                        //setting data to view
                        setData(city_name, localtime, text, url, temp, windSpeed, today_date);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Sorry...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Error", "Error after Response: " + anError.toString());
            }
        });
    }

    private void setToPopup() {
        int _uv = 0;
        try {
            _uv = current.getInt("uv");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String sun_rise = null, sun_set = null, moon_rise = null, moon_set = null;
        try {
            JSONObject astro = today.getJSONObject("astro");
            sun_rise = astro.getString("sunrise");
            sun_set = astro.getString("sunset");
            moon_rise = astro.getString("moonrise");
            moon_set = astro.getString("moonset");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String temp_C_s = temp + " °C";
        pop_temp_c.setText(temp_C_s);
        String temp_s = tempF + " °F";
        pop_temp_f.setText(temp_s);
        String windSpeed_s = windSpeed + " km/h";
        pop_wind_speed.setText(windSpeed_s);
        pop_cloud.setText(text);
        pop_uv.setText(String.valueOf(_uv));
        pop_sun_rise.setText(sun_rise);
        pop_sun_set.setText(sun_set);
        pop_moon_rise.setText(moon_rise);
        pop_moon_set.setText(moon_set);
    }


    private void setOvrmDate(String ovrm_date) {
        dateFormatter.formatTime(ovrm_date, "yyyy-MM-dd", "dd-MM-yy", Title_overmDate);


    }

    private void setTmrDate(String tmrw_date) {
        dateFormatter.formatTime(tmrw_date, "yyyy-MM-dd", "dd-MM-yy", Title_tomorrowDate);

    }

    private void overmorrowData(JSONArray overmorrow_hour) {
        ArrayList<conditonData> conditonDataArrayList2 = new ArrayList<>();
        for (int i = overmorrow_hour.length() - 1; i >= 0; i--) {
            try {
                JSONObject overmorrow_array = overmorrow_hour.getJSONObject(i);
                weatherData overmorrow_data = gson.fromJson(overmorrow_array.toString(), weatherData.class);
                JSONObject overmorrow_condition = overmorrow_array.getJSONObject("condition");
                conditonData overmorrow_condition_view = gson.fromJson(overmorrow_condition.toString(), conditonData.class);
                conditonDataArrayList2.add(overmorrow_condition_view);
                weatherDataArrayList2.add(overmorrow_data);
                overmorrowAdapter adapter3 = new overmorrowAdapter(weatherDataArrayList2, conditonDataArrayList2, MainActivity.this);
                ovrm_rv.setAdapter(adapter3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void tomorrowData(JSONArray tomorrow_hour) {
        ArrayList<conditonData> conditonDataArrayList1 = new ArrayList<>();
        for (int i = tomorrow_hour.length() - 1; i >= 0; i--) {

            try {
                JSONObject tomorrowData = tomorrow_hour.getJSONObject(i);
                JSONObject tmr_condition = tomorrowData.getJSONObject("condition");
                conditonData tmr_condition_view = gson.fromJson(tmr_condition.toString(), conditonData.class);
                weatherData tomorrow_data = gson.fromJson(tomorrowData.toString(), weatherData.class);
                //For tomorrow condition
                conditonDataArrayList1.add(tmr_condition_view);
                //For tomorrow data
                weatherDataArrayList1.add(tomorrow_data);
                tomorrowAdapter adapter2 = new tomorrowAdapter(weatherDataArrayList1, conditonDataArrayList1, MainActivity.this);
                tmr_rv.setAdapter(adapter2);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void todaySetData(JSONArray tdy_hour) {
        for (int i = tdy_hour.length() - 1; i >= 0; i--) {
            try {
                JSONObject tdyData = tdy_hour.getJSONObject(i);
                JSONObject condition = tdyData.getJSONObject("condition");
                weatherData today_weather = gson.fromJson(tdyData.toString(), weatherData.class);
                conditonData condito_data = gson.fromJson(condition.toString(), conditonData.class);
                weatherDataList.add(today_weather);
                conditonDataArrayList.add(condito_data);
                //Setting adapter
                weatherAdapter adapter = new weatherAdapter(MainActivity.this, weatherDataList, conditonDataArrayList);
                recyclerView.setAdapter(adapter);
                //        Log.d("Today Hour", "todaySetData: "+tdyData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //Setting data to Views
    private void setData(String city, String localTime, String weatherType, String url, int temp, int windSpeed, String today_date) {

        convetTime(localTime);
        convetTime2(today_date);
        localityTv.setText(city);
        weatherTypeTv.setText(weatherType);
        Picasso.get().load("https:" + url).into(weatherIconImgv);
        String temp_s = temp + " °C";
        tempTv.setText(temp_s);
        String windSp_s = windSpeed + " km/h";
        windTv.setText(windSp_s);
    }

    //Title Date
    private void convetTime2(String today_date) {
        dateFormatter.formatTime(today_date, "yyyyy-MM-dd", "dd-MM-yy", Title_todayDate);
    }

    //Converting time for details card
    private void convetTime(String localTime) {
        dateFormatter.formatTime(localTime, "yyyyy-MM-dd hh:mm", "hh:mm a", weatherDateTv);

    }

    //Finding Views through id
    private void findById() {
        tempTv = findViewById(R.id.card_temp_id);
        windTv = findViewById(R.id.card_wind_id);
        localityTv = findViewById(R.id.card_locality_id);
        weatherDateTv = findViewById(R.id.card_weather_date_id);
        weatherTypeTv = findViewById(R.id.card_weather_type_id);
        weatherIconImgv = findViewById(R.id.card_weather_icon_id);
        recyclerView = findViewById(R.id.rv_id);
        Title_todayDate = findViewById(R.id.today_date_title_id);
        tmr_rv = findViewById(R.id.tomorrow_rv_id);
        ovrm_rv = findViewById(R.id.overm_rv_id);
        Title_tomorrowDate = findViewById(R.id.tomorrow_date_title_id);
        Title_overmDate = findViewById(R.id.trd_date_title_id);
        searchBtn = findViewById(R.id.search_button_id);
        card_view_btn = findViewById(R.id.card1);
        card_fb_shimmer = findViewById(R.id.crd_fb_shimmer_id);
        card_details = findViewById(R.id.card_details_id);
        rv_parent_tday = findViewById(R.id.rv_shimmen_card_layout_id);
        tmrw_parent = findViewById(R.id.tmrw_card_shimmem_id);
        over_shimmem_layout = findViewById(R.id.overm_shimmen_layout_id);
    }

}