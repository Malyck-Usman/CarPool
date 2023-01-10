package com.sharerideexpense.easycarpool.classes;

public class UserInfo {
    private String name;
    private String fcm_token;
    private String email;
    private String photo_uri;
    private String phone_no;
    private String vehicle_type;
    private String vehicle_name;
    private String vehicle_no;
    private boolean profile_updated;
//    private boolean verified;


    public UserInfo() {
    }

    public UserInfo(String name, String email, String photo_uri, String driver_fcm_token,boolean profile_updated) {
        this.name = name;
        this.email = email;
        this.photo_uri = photo_uri;
        this.fcm_token = driver_fcm_token;
    }

    public boolean isProfile_updated() {
        return profile_updated;
    }

    public void setProfile_updated(boolean profile_updated) {
        this.profile_updated = profile_updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }
}
