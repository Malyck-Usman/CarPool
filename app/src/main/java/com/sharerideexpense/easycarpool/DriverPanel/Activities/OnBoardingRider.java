package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharerideexpense.easycarpool.DriverPanel.Adaptors.OnBoardingAdaptor;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;

public class OnBoardingRider extends AppCompatActivity {
    private ViewPager viewPager;
    private OnBoardingAdaptor adapter;
    private LinearLayout layout_dots,ll_get_started;
    private TextView[] tv_dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_rider);
        viewPager = findViewById(R.id.vp_rider);
        layout_dots = findViewById(R.id.layout_dots);
        ll_get_started=findViewById(R.id.ll_get_started);
        ll_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MySharedPreferences(OnBoardingRider.this).setRiderOnBoarding(true);
                startActivity(new Intent(OnBoardingRider.this,MyRides.class));
                finish();
            }
        });

        adapter = new OnBoardingAdaptor(OnBoardingRider.this);
        viewPager.setAdapter(adapter);

        addDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if(position==2)
                    ll_get_started.setVisibility(View.VISIBLE);
                else
                    ll_get_started.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void addDots(int position){
        tv_dots = new TextView[3];
        layout_dots.removeAllViews();
        for (int i =0 ; i < tv_dots.length; i++){
            tv_dots[i] = new TextView(OnBoardingRider.this);
            tv_dots[i].setText(Html.fromHtml("&#8226;"));
            tv_dots[i].setTextSize(35);
            tv_dots[i].setTextColor(getResources().getColor(R.color.white50));

            layout_dots.addView(tv_dots[i]);
        }
        if (tv_dots.length > 0){
            tv_dots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }
}