package com.sharerideexpense.easycarpool.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.sharerideexpense.easycarpool.R;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity implements AppOpen {

    private FirebaseAuth mAuth;
    TextInputLayout edt_email_forgot;
    private CardView btn_reset;
    private String email;
    AlertDialog dialog;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Init();
        setToolbar();
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ValidateEmail()) {
                    return;
                } else {
                    mAuth.sendPasswordResetEmail(email)

                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showDialog();
//                                        ads();
                                    } else {
                                        Toast.makeText(ForgetPassword.this, "There is no user record corresponding to this email!", Toast.LENGTH_SHORT).show();
                                        Log.d("TAG","Error resetting e mail "+ Objects.requireNonNull(task.getException()).getMessage());
                                    }

                                }
                            });
                }

            }
        });
    }

    private boolean ValidateEmail() {
        email = Objects.requireNonNull(edt_email_forgot.getEditText()).getText().toString().trim();

        if (email.isEmpty()) {
            edt_email_forgot.setError("E-mail is required ");
            //edt_Email.isFocused();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            edt_email_forgot.setError("Invalid e-mail address");
        } else {
            edt_email_forgot.setError(null);
            return true;
        }

        return false;
    }

    private void Init() {
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);

        mAuth = FirebaseAuth.getInstance();
//        adContainer = findViewById(R.id.ad_container);
        edt_email_forgot = findViewById(R.id.edt_email_forgot);
        btn_reset = findViewById(R.id.btn_reset);

    }
private void showDialog(){
    dialog = new AlertDialog.Builder(this)
            .setTitle("Password Reset")
            .setMessage("A Password reset Email has been sent to you. Check you email and follow the guide to reset your password")
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ForgetPassword.this, Signin.class));
                    finish();
                }
            }).show();
}
//    private void ads() {
//        ApplicationController.adPos++;
//        if (ApplicationController.adPos >= 1) {
//            ApplicationController.adPos = 0;
//            InterstitialAdSingleton mInterstitialAdSingleton = InterstitialAdSingleton.getInstance(this);
//            mInterstitialAdSingleton.showInterstitial(this);
//
//        }
//    }

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
    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ImageView btn_toolbar = toolbar.findViewById(R.id.iv_toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Forget Password");
        btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }
}