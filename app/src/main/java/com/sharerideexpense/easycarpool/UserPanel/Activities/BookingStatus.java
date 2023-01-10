package com.sharerideexpense.easycarpool.UserPanel.Activities;

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
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.fragments.BookingRequestApproved;
import com.sharerideexpense.easycarpool.UserPanel.fragments.BookingRequestDeclined;
import com.sharerideexpense.easycarpool.UserPanel.fragments.BookingRequestsSent;
import com.google.android.material.tabs.TabLayout;

public class BookingStatus extends AppCompatActivity implements TabLayout.OnTabSelectedListener, AppOpen {
    FragmentManager fm;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;
    TabLayout.Tab tb_new, tb_accepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);
        Init();
    }

    private void Init() {
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);

        InitTopBar();
        TabLayout tl = findViewById(R.id.tl_booking_status);
        tl.addOnTabSelectedListener(this);
//        Objects.requireNonNull(tl.getTabAt(0)).getOrCreateBadge();
        fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fl_container_booking_status, new BookingRequestApproved()).commit();
    }

    private void InitTopBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv_toolbar = findViewById(R.id.tv_toolbar);
        tv_toolbar.setText("My Bookings");
        setSupportActionBar(toolbar);
        ImageView iv_toolbar = findViewById(R.id.iv_toolbar);
        iv_toolbar.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                fm.beginTransaction().replace(R.id.fl_container_booking_status, new BookingRequestApproved()).commit();
                break;
            case 1:
                fm.beginTransaction().replace(R.id.fl_container_booking_status, new BookingRequestsSent()).commit();
                break;
            case 2:
                fm.beginTransaction().replace(R.id.fl_container_booking_status, new BookingRequestDeclined()).commit();
                break;
        }
        ShowInterstitialAd();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BookingStatus.this, UserSearchActivity.class));
        finish();
    }
    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }
}