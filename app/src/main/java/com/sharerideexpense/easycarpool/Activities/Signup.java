package com.sharerideexpense.easycarpool.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class Signup extends AppCompatActivity implements View.OnClickListener, AppOpen {
    TextInputLayout edt_Name, edt_Email, edt_Password;
    CardView btn_signup;
    TextView tv_signin;
    String email, password;
    FirebaseAuth mAuth;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;
    String mFCMToken;
    FirebaseFirestore mDBRef;
    MySharedPreferences preferences;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Init();
    }

    private void Init() {
        dialog=new ProgressDialog(this);
        dialog.setTitle("processing");
        dialog.setMessage("Please wait  . . .");
        preferences = new MySharedPreferences(this);
        mDBRef = FirebaseFirestore.getInstance();
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);


        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Sign Up");
        ImageView iv_back = toolbar.findViewById(R.id.iv_toolbar);
        iv_back.setOnClickListener(this);
        setSupportActionBar(toolbar);
        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(this);
        edt_Email = findViewById(R.id.edt_email_signup);
        edt_Password = findViewById(R.id.edt_password_signup);
        tv_signin = findViewById(R.id.tv_signin);
        tv_signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                if (!ValidateEmail() | !ValidatePassword()) {
                    return;
                } else {
                    CreateNewUser();
                }
                break;
            case R.id.iv_toolbar:
                onBackPressed();
                break;
            case R.id.tv_signin:
                startActivity(new Intent(Signup.this, Signin.class));
                finish();
        }
    }

    private void CreateNewUser() {
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");


                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                InitFCMToken(user);
                                UpdateUserDataInDatabase(user);
                            }
                        } else {
                            dialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed. Email Address is already in use ", Toast.LENGTH_LONG).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    private boolean ValidateEmail() {
        email = edt_Email.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            edt_Email.setError("E-mail is required ");
            //edt_Email.isFocused();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            edt_Email.setError("Invalid e-mail address");
        } else {
            edt_Email.setError(null);
            return true;
        }

        return false;
    }

    private boolean ValidatePassword() {
        password = edt_Password.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            edt_Password.setError("password is required ");
            //edt_password.isFocused();
        } else if (password.length() < 4) {
            edt_Password.setError("Password can't be less then 4 characters ");

        } else {
            edt_Password.setError(null);
            return true;
        }

        return false;
    }

    private void CheckUserInfo(FirebaseUser user) {
        preferences.setUserId(user.getUid());
        preferences.setUserEmail(user.getEmail());
        preferences.setUserFcm(mFCMToken);
        if (user.getPhotoUrl() != null) {
            preferences.setUserImageUri(Objects.requireNonNull(user.getPhotoUrl()).toString());
            preferences.setUserOnlineImageUri(Objects.requireNonNull(user.getPhotoUrl()).toString());
        }
        preferences.setUserName(user.getDisplayName());
        dialog.dismiss();
        startActivity(new Intent(Signup.this, SelectMode.class));
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

    private void InitFCMToken(FirebaseUser fbUser) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    mFCMToken = task.getResult();
                    mDBRef.collection(getString(R.string.USERS)).document(fbUser.getUid()).update(getString(R.string.FCM_TOKEN), mFCMToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "FCM Updated successfully");
                                    preferences.setUserFcm(mFCMToken);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "Failed to update fcm " + e.getMessage());
                                }
                            });

                    Log.d("TAG", " FCM Token " + task.getResult());


                } else {
                    Log.d("TAG", "Error getting FCM Token " + task.getException());

                }
            }
        });
    }
private  void UpdateUserDataInDatabase(FirebaseUser user){
   mDBRef.collection(getString(R.string.USERS)).document(user.getUid()).set(new UserInfo(user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), mFCMToken, false))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    CheckUserInfo(user);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Signup.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Signin:Cannot insert data " + e.getMessage());
                }
            });
}
}