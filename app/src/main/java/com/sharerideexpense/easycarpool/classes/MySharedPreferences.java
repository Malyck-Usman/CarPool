package com.sharerideexpense.easycarpool.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPreferences {
    private String USER_ID = "user_id";
    private String USER_NAME = "user_name";
    private String USER_PHONE_NO = "user_phone_no";
    private String USER_EMAIL = "user_email";
    private String USER_IMAGE_URI = "user_image";
    private String USER_ONLINE_IMAGE_URI = "user_online_image";
    private String USER_MODE="user_mode";
    private String VEHICLE_TYPE="vehicle_type";
    private String VEHICLE_NAME="vehicle_name";
    private String VEHICLE_NO="vehicle_no";
    private String PROFILE_UPDATED="profile_updated";
    private String USER_FCM="user_fcm";
    private String RIDER_ON_BOARDING="rider_on_boarding";
    private String PASSENGER_ON_BOARDING="passenger_on_boarding";
    private String PLACES_API_KEY="places_api_key";
    private String FCM_SERVER_KEY="fcm_server_key";
    private String COUNTRY_CODE="country_code";
    private String REMOTE_VERSION_CODE="remote_version_code";
    SharedPreferences preferences;
    Context context;

    public MySharedPreferences(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }
    public void clearData(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, "");
        editor.putString(USER_NAME, "");
        editor.putString(USER_PHONE_NO, "");
        editor.putString(USER_EMAIL, "");
        editor.putString(USER_IMAGE_URI, "");
        editor.putString(USER_ONLINE_IMAGE_URI, "");
        editor.putString(USER_MODE, "");
        editor.putString(VEHICLE_TYPE, "");
        editor.putString(VEHICLE_NAME, "");
        editor.putString(VEHICLE_NO, "");
        editor.putBoolean(PROFILE_UPDATED, false);
        editor.apply();

    }

    public void setUserId(String u_id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, u_id);
        editor.apply();
    }
    public void setREMOTE_VERSION_CODE(long version_code) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(REMOTE_VERSION_CODE, version_code);
        editor.apply();
    }
    public void setCountryCode(String cc) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(COUNTRY_CODE, cc);
        editor.apply();
    }
    public void setPlacesApiKey(String p_key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PLACES_API_KEY, p_key);
        editor.apply();
    }
    public void setFcmSeverKey(String fcm_key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FCM_SERVER_KEY, fcm_key);
        editor.apply();
    }
    public void setUserName(String u_name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, u_name);
        editor.apply();
    }
    public void setUserPhoneNo(String u_no) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_PHONE_NO, u_no);
        editor.apply();
    }
    public void setUserEmail(String u_email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_EMAIL, u_email);
        editor.apply();
    }
    public void setUserFcm(String u_fcm) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_FCM, u_fcm);
        editor.apply();
    }
    public void setUserImageUri(String u_image_uri) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_IMAGE_URI, u_image_uri);
        editor.apply();
    }
    public void setUserOnlineImageUri(String u_image_uri) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ONLINE_IMAGE_URI, u_image_uri);
        editor.apply();
    }
    public void setUserMode(String u_mode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_MODE, u_mode);
        editor.apply();
    }
    public void setVehicleType(String vehicle_type) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VEHICLE_TYPE, vehicle_type);
        editor.apply();
    }
    public void setVehicleName(String v_name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VEHICLE_NAME, v_name);
        editor.apply();
    }
    public void setVehicleNo(String v_no) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VEHICLE_NO, v_no);
        editor.apply();
    }
    public void setProfileUpdated(boolean p_updated) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PROFILE_UPDATED, p_updated);
        editor.apply();
    }
    public void setPassengerOnBoarding(boolean p_on_boarding) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PASSENGER_ON_BOARDING, p_on_boarding);
        editor.apply();
    }
    public void setRiderOnBoarding(boolean r_on_boarding) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(RIDER_ON_BOARDING, r_on_boarding);
        editor.apply();
    }

    public String getUserId() {
        return preferences.getString(USER_ID, "");

    }
    public String getPlacesApiKey() {
        return preferences.getString(PLACES_API_KEY, "");

    }
    public String getFcmServerKey() {
        return preferences.getString(FCM_SERVER_KEY, "");

    }
    public String getUserFcm() {
        return preferences.getString(USER_FCM, "");

    }
    public long getRemoteVersionCode() {
        return preferences.getLong(REMOTE_VERSION_CODE, 0);

    }
    public String getUserName() {
        return preferences.getString(USER_NAME, "");

    }
    public String getUserPhoneNo() {
        return preferences.getString(USER_PHONE_NO, "");

    }
    public String getUserEmail() {
        return preferences.getString(USER_EMAIL, "");

    }   public String getUserImageUri() {
        return preferences.getString(USER_IMAGE_URI, "");

    }  public String getUserOnlineImageUri() {
        return preferences.getString(USER_ONLINE_IMAGE_URI, "");

    }
    public String getUserMode() {
        return preferences.getString(USER_MODE, "");

    }   public String getVehicleType() {
        return preferences.getString(VEHICLE_TYPE, "");

    }   public String getVehicleName() {
        return preferences.getString(VEHICLE_NAME, "");

    }
    public String getVehicleNo() {
        return preferences.getString(VEHICLE_NO, "");

    }
    public String getCountryCode() {
        return preferences.getString(COUNTRY_CODE, "");

    }
    public boolean getProfileUpdated() {
        return preferences.getBoolean(PROFILE_UPDATED, false);
    }
    public boolean getPassengerOnBoarding() {
        return preferences.getBoolean(PASSENGER_ON_BOARDING, false);
    }
    public boolean getRiderOnBoarding() {
        return preferences.getBoolean(RIDER_ON_BOARDING, false);
    }

}
