package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.sharerideexpense.easycarpool.DriverPanel.fragments.AcceptedBookingRequests;
import com.sharerideexpense.easycarpool.DriverPanel.fragments.NewBookingRequests;
import com.google.android.material.tabs.TabLayout;
import com.sharerideexpense.easycarpool.R;

public class RideRequests extends AppCompatActivity implements TabLayout.OnTabSelectedListener , AppOpen {
    FragmentManager fm;
    TabLayout tabLayout;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_requests);
        Init();

    }

    private void Init() {
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);
        tabLayout = findViewById(R.id.tl_ride_request);
        tabLayout.addOnTabSelectedListener(this);
        setToolbar();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fl_container_br, new NewBookingRequests()).commit();
        tabLayout.selectTab(tabLayout.getTabAt(0));
        Intent intent = getIntent();
        if (intent.hasExtra("identifier")) {
            if (intent.getStringExtra("identifier").equals("BookingRequestCancelled")) {
                tabLayout.selectTab(tabLayout.getTabAt(1));
            } else if (intent.getStringExtra("identifier").equals("NewBookingRequestReceived")) {
                tabLayout.selectTab(tabLayout.getTabAt(0));
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ImageView btn_toolbar = toolbar.findViewById(R.id.iv_toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Ride Requests");
        btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                ShowInterstitialAd();
                fm.beginTransaction().replace(R.id.fl_container_br, new NewBookingRequests()).commit();
                break;
            case 1:
                ShowInterstitialAd();
                fm.beginTransaction().replace(R.id.fl_container_br, new AcceptedBookingRequests()).commit();

                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void restoreAds() {
        fl_ad.setVisibility(View.VISIBLE);
        margin.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeAds() {
        fl_ad.setVisibility(View.INVISIBLE);
        margin.setVisibility(View.INVISIBLE);
    }
    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }
}