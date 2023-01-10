package com.sharerideexpense.easycarpool.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharerideexpense.easycarpool.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        InitTopBar();
    }

    private void InitTopBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv_toolbar = findViewById(R.id.tv_toolbar);
        tv_toolbar.setText("Privacy Policy");
        setSupportActionBar(toolbar);
        ImageView iv_toolbar = findViewById(R.id.iv_toolbar);
        iv_toolbar.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}