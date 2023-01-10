package com.sharerideexpense.easycarpool.Activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sharerideexpense.easycarpool.Ads.AppOpenManager;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;


public class ApplicationController extends MultiDexApplication {
    public static final String TAG = ApplicationController.class.getSimpleName();
    public static int adPos = 0;
    private static Context appContext;
    private static ApplicationController mInstance;
    public Typeface faceContent, faceHead;
    public AppOpenManager appOpenManager;
    public long remoteVersionCode = 0;
    public static FirebaseAuth mAuth;
    public static GoogleSignInClient googleSignInClient;
    MySharedPreferences prefs;
    private FirebaseRemoteConfig remoteConfig;



   /* public static RewardedInterstitialAd rewardedInterstitialAd;
    public static boolean isRewardedLoaded = false;*/

    @SuppressLint("MissingPermission")
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        signInRequest();

        prefs = new MySharedPreferences(this);
        if (prefs.getFcmServerKey().equals("") || prefs.getPlacesApiKey().equals("")) {
            getKeys();
        }
        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(this);
        //setTypeFace();
        appContext = getApplicationContext();
       /* MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        //loadAd();
                    }
                });*/
        appOpenManager = new AppOpenManager(this);
        /*InterstitialAdSingleton mInterstitialAdSingleton = InterstitialAdSingleton.getInstance(this);
        mInterstitialAdSingleton.firstInterstitialLoad();*/
        mInstance = this;
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(21600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                PackageManager manager = getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getPackageName(), 0);
                    if (info != null) {
                        remoteVersionCode = Long.parseLong(remoteConfig.getString(getString(R.string.APP_VERSION)));
                        prefs.setREMOTE_VERSION_CODE(remoteVersionCode);

                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Config params updated failed: " + e.getMessage());

            }
        }).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

            }
        });
    }

    public void getKeys() {
        DatabaseReference mDBRef = FirebaseDatabase.getInstance().getReference();

        mDBRef.child(getString(R.string.API_KEYS)).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Log.d("TAG", "data snapshot " + dataSnapshot);
                        prefs.setPlacesApiKey((String) dataSnapshot.child(getString(R.string.PLACES_API_KEY)).getValue());
                        prefs.setFcmSeverKey((String) dataSnapshot.child(getString(R.string.FCM_KEY)).getValue());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed to load api keys " + e.getMessage());
                    }
                });
    }



  /*  public void loadAd() {

        RewardedInterstitialAd.load(this, getResources().getString(R.string.rewarded),
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        isRewardedLoaded = true;
                        Log.e("TAG", "onAdLoaded");
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                // Toast.makeText(getApplicationContext(), "onAdFailedToShowFullScreenContent Called", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                // Toast.makeText(getApplicationContext(), "onAdShowedFullScreenContent Called", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                if (MainFragment.allowReward) {
                                    MainActivity.startLiveChatActivity();
                                    MainFragment.allowReward = false;
                                }
                                // Toast.makeText(getApplicationContext(), "DISMiss Called", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad");
                        isRewardedLoaded = false;
                    }
                });
    }
*/

    private void setTypeFace() {

        //faceContent = Typeface.createFromAsset(getAssets(), "Bariol Regular Webfont.ttf");
        faceHead = Typeface.createFromAsset(getAssets(), "Bariol Regular Webfont.ttf");

    }

    public static synchronized ApplicationController getInstance() {
        ApplicationController applicationController;
        synchronized (ApplicationController.class) {
            applicationController = mInstance;
        }
        return applicationController;
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void initNativeAd(final TemplateView templateView) {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_nativeid));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                templateView.setVisibility(View.VISIBLE);
                templateView.setNativeAd(nativeAd);
            }
        });

        AdLoader adLoader = builder.build();
        AdRequest adRequest = new AdRequest.Builder().build();
        adLoader.loadAd(adRequest);
    }

    public void signInRequest() {
        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}