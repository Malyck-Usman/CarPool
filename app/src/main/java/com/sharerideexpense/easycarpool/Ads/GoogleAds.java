package com.sharerideexpense.easycarpool.Ads;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.sharerideexpense.easycarpool.R;

public class GoogleAds {

    public static boolean interstitialShowed = false;
    private AdView adview;
    private final Context context;
    private boolean isShowInterstitial = false;
    private boolean isInterstitialLoaded = false;
    private AdManagerInterstitialAd mInterstitialAd;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;
    AdSize adSize;


    public GoogleAds(Context context) {
        this.context = context;
        isInterstitialLoaded = false;
    }

    public GoogleAds(Context context, AdView adview) {
        super();
        this.context = context;
        this.adview = adview;
        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }
        adSize = getAdSize();
//        adview.setAdSize(adSize);
        setAdsListener();

    }

    public void startAdsCall() {
        Log.i(LOG_TAG, "Starts");

        adview.resume();
        adsHandler.removeCallbacks(sendUpdatesToUI);
        adsHandler.postDelayed(sendUpdatesToUI, 0);
    }

    public void stopAdsCall() {
        Log.e(LOG_TAG, "Ends");
        adsHandler.removeCallbacks(sendUpdatesToUI);
        adview.pause();
    }

    public void destroyAds() {
        Log.e(LOG_TAG, "Destroy");
        adview.destroy();
        adview = null;
    }

    private void updateUIAds() {
        if (isNetworkConnected()) {
            //AdRequest adRequest = new AdRequest.Builder().build();
            // Add a test device to show Test Ads
            // AdRequest adRequest = new AdRequest.Builder()
                    /*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    //for s5
                      //.addTestDevice("1F14AA595641605BA11AD92906D5C415")
                    //for Qmob
                  .addTestDevice("0733392D109781D35E277ECC070010D8")*/
            AdRequest adRequest = new AdRequest.Builder().build();

            // Step 4 - Set the adaptive ad size on the ad view.


            adview.loadAd(adRequest);
        } else {
            timerValue = networkRefreshTime;
            adview.setVisibility(View.GONE);
            adsHandler.removeCallbacks(sendUpdatesToUI);
            adsHandler.postDelayed(sendUpdatesToUI, timerValue);
        }
    }

    private void setAdsListener() {
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(LOG_TAG, getErrorReason(loadAdError.getCode()));
                adview.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                Log.d(LOG_TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");

                adview.setVisibility(View.VISIBLE);
            }
        });
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.v(LOG_TAG, "Recall");
            updateUIAds();
        }
    };

    public boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }


    public void callInterstetialAds(boolean showAd) {
        final boolean chkShowAd = showAd;
        isInterstitialLoaded = false;
        isShowInterstitial = true;
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAd.load(context, context.getString(R.string.admob_interstitialid), adRequest, new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("TAG", "onAdLoaded");
                //   Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                isInterstitialLoaded = true;
                /*if (chkShowAd && isShowInterstitial)
                    showInterstetialAds(activity);*/

                interstitialAd.setFullScreenContentCallback(
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                // mInterstitialAd = null;
                                interstitialShowed = false;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                // mInterstitialAd = null;
                                interstitialShowed = false;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.d("TAG", "The ad was shown.");
                                interstitialShowed = true;
                            }
                        });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("TAG", loadAdError.getMessage());
                mInterstitialAd = null;
                @SuppressLint("DefaultLocale") String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
            }
        });
    }

    public void showInterstetialAds(Activity activity) {
        isShowInterstitial = true;
        isInterstitialLoaded = false;
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            Log.e("interstitial Ad", "No Ad");

        }
    }

    public void cancelIntersitialAds() {
        isShowInterstitial = false;
    }

    public boolean isInterstitialAdLoaded() {

        return isInterstitialLoaded;
    }

    public void onBannerClick() {

//        sendEventAnalytics("QR AD Clicked", "Clicked");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //	intent.setData(Uri.parse(Constants.BANNER_IMAGE_URL + Constants.URL_REFFERER));
        context.startActivity(intent);
    }

//    public void sendEventAnalytics(String eventCategory, String eventAction) {
//
//        AnalyticSingaltonClass.getInstance(context).sendEventAnalytics(eventCategory, eventAction);
//    }


    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
