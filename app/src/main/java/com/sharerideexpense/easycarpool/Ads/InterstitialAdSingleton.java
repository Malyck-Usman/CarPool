package com.sharerideexpense.easycarpool.Ads;

import android.app.Activity;
import android.content.Context;

public class InterstitialAdSingleton {

        private static InterstitialAdSingleton mInstance = null;
        private GoogleAds mGoogleAds;
        private Context mContext;

        public static InterstitialAdSingleton getInstance(Context context) {

            if (mInstance == null) {
                mInstance = new InterstitialAdSingleton(context);
            }
            return mInstance;
        }

        private InterstitialAdSingleton(Context context) {

            mContext = context.getApplicationContext();
            mGoogleAds = new GoogleAds(mContext);
        }

        public void firstInterstitialLoad() {
            mGoogleAds.callInterstetialAds(false);
        }

        public void showInterstitial(Activity activity) {

            mGoogleAds.showInterstetialAds(activity);
            mGoogleAds.callInterstetialAds(false);
        }

        public boolean isInterstitialAdLoaded() {
            return mGoogleAds.isInterstitialAdLoaded();
        }

        public void cancelInterstitialAd() {
            mGoogleAds.cancelIntersitialAds();
        }

}
