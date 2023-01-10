package com.sharerideexpense.easycarpool.Ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.sharerideexpense.easycarpool.R;

import java.util.Date;

/**
 * Prefetches App Open Ads.
 */

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "AppOpenManager";
    //private static final String AD_UNIT_ID = "ca-app-pub-9800935656438737/6833848924";
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private final ApplicationController myApplication;
    private static boolean isShowingAd = false;
    private long loadTime = 0;
    private static boolean firstTym = true;
    String className;
    AppOpen appOpen = null;

    /**
     * Constructor
     */
    public AppOpenManager(ApplicationController myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void setAppOpen(AppOpen appOpen) {
        this.appOpen = appOpen;
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        try {
            showAdIfAvailable();
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        // We will implement this below.
        if (isAdAvailable()) {
            return;
        }

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        AppOpenManager.this.appOpenAd = appOpenAd;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Toast.makeText(myApplication, loadAdError.toString(),Toast.LENGTH_LONG).show();
                    }

                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, myApplication.getResources().getString(R.string.admod_appopen), request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        className = currentActivity.getClass().getSimpleName();
        if (!isShowingAd && isAdAvailable() && !className.equals("SplashActivity") && !GoogleAds.interstitialShowed) {
            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                            appOpen.restoreAds();
                            // restoreAds(className);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.d("TAG", "failed to load add " + adError);
                            //    Toast.makeText(myApplication, adError.toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;

                        }
                    };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);
            appOpen.closeAds();
            //closeAds(className);

        } else {
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * ActivityLifecycleCallback methods
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }
}