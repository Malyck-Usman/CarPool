package com.sharerideexpense.easycarpool.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.DriverPanel.Activities.MyRides;
import com.sharerideexpense.easycarpool.DriverPanel.Activities.OnBoardingRider;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Activities.OnBoardingPassenger;
import com.sharerideexpense.easycarpool.UserPanel.Activities.UserSearchActivity;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.databinding.ActivitySelectModeBinding;

public class SelectMode extends AppCompatActivity implements View.OnClickListener {
    ActivitySelectModeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        binding = ActivitySelectModeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.llPassenger.setOnClickListener(this);
        binding.llRider.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        MySharedPreferences prefs = new MySharedPreferences(this);
//        ShowInterstitialAd();
        if (v == binding.llPassenger) {
            prefs.setUserMode(getString(R.string.PASSENGER_MODE));
            if(prefs.getPassengerOnBoarding()){
            startActivity(new Intent(SelectMode.this, UserSearchActivity.class));
            }else {
            startActivity(new Intent(SelectMode.this, OnBoardingPassenger.class));
            }
            finish();
        } else if (v == binding.llRider) {
            prefs.setUserMode(getString(R.string.RIDER_MODE));
            if(prefs.getRiderOnBoarding()){
            startActivity(new Intent(SelectMode.this, MyRides.class));
            }else {
                startActivity(new Intent(SelectMode.this, OnBoardingRider.class));
            }
            finish();
        }
    }

//    private void ShowInterstitialAd() {
//        InterstitialAdSingleton.getInstance(this).showInterstitial(this);
//
//    }
}