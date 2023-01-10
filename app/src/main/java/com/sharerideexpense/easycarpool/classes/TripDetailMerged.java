package com.sharerideexpense.easycarpool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class TripDetailMerged implements Parcelable {
    private String driver_id;
    private String trip_id;
    private String name;
    private String fcm_token;
    private String photo_uri;
    private String phone_no;
    private String vehicle_type;
    private String vehicle_name;
    private String vehicle_no;
    private String trip_type;
    private String pick_location;
    private GeoPoint pick_point;
    private String date;
    private String time;
    private String drop_location;
    private GeoPoint drop_point;
    private int seating_capacity;
    private int remaining_seats;
    private int charges_per_km;
    private String route;
    private int male;
    private int female;
    private int kids;

    public TripDetailMerged() {
    }

    public TripDetailMerged(String driver_id, String trip_id, String name, String fcm_token, String photo_uri, String phone_no, String vehicle_type, String vehicle_name, String vehicle_no, String trip_type, String pick_location, GeoPoint pick_point, String date, String time, String drop_location, GeoPoint drop_point, int seating_capacity, int remaining_seats, int charges_per_km, String route, int male, int female, int kids) {
        this.driver_id = driver_id;
        this.trip_id = trip_id;
        this.name = name;
        this.fcm_token = fcm_token;
        this.photo_uri = photo_uri;
        this.phone_no = phone_no;
        this.vehicle_type = vehicle_type;
        this.vehicle_name = vehicle_name;
        this.vehicle_no = vehicle_no;
        this.trip_type = trip_type;
        this.pick_location = pick_location;
        this.pick_point = pick_point;
        this.date = date;
        this.time = time;
        this.drop_location = drop_location;
        this.drop_point = drop_point;
        this.seating_capacity = seating_capacity;
        this.remaining_seats = remaining_seats;
        this.charges_per_km = charges_per_km;
        this.route = route;
        this.male = male;
        this.female = female;
        this.kids = kids;
    }


    protected TripDetailMerged(Parcel in) {
        driver_id = in.readString();
        trip_id = in.readString();
        name = in.readString();
        fcm_token = in.readString();
        photo_uri = in.readString();
        phone_no = in.readString();
        vehicle_type = in.readString();
        vehicle_name = in.readString();
        vehicle_no = in.readString();
        trip_type = in.readString();
        pick_location = in.readString();
        date = in.readString();
        time = in.readString();
        drop_location = in.readString();
        seating_capacity = in.readInt();
        remaining_seats = in.readInt();
        charges_per_km = in.readInt();
        route = in.readString();
        male = in.readInt();
        female = in.readInt();
        kids = in.readInt();
    }

    public static final Creator<TripDetailMerged> CREATOR = new Creator<TripDetailMerged>() {
        @Override
        public TripDetailMerged createFromParcel(Parcel in) {
            return new TripDetailMerged(in);
        }

        @Override
        public TripDetailMerged[] newArray(int size) {
            return new TripDetailMerged[size];
        }
    };

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

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getPick_location() {
        return pick_location;
    }

    public void setPick_location(String pick_location) {
        this.pick_location = pick_location;
    }

    public GeoPoint getPick_point() {
        return pick_point;
    }

    public void setPick_point(GeoPoint pick_point) {
        this.pick_point = pick_point;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public GeoPoint getDrop_point() {
        return drop_point;
    }

    public void setDrop_point(GeoPoint drop_point) {
        this.drop_point = drop_point;
    }

    public int getSeating_capacity() {
        return seating_capacity;
    }

    public void setSeating_capacity(int seating_capacity) {
        this.seating_capacity = seating_capacity;
    }

    public int getRemaining_seats() {
        return remaining_seats;
    }

    public void setRemaining_seats(int remaining_seats) {
        this.remaining_seats = remaining_seats;
    }

    public int getCharges_per_km() {
        return charges_per_km;
    }

    public void setCharges_per_km(int charges_per_km) {
        this.charges_per_km = charges_per_km;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getMale() {
        return male;
    }

    public void setMale(int male) {
        this.male = male;
    }

    public int getFemale() {
        return female;
    }

    public void setFemale(int female) {
        this.female = female;
    }

    public int getKids() {
        return kids;
    }

    public void setKids(int kids) {
        this.kids = kids;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(driver_id);
        dest.writeString(trip_id);
        dest.writeString(name);
        dest.writeString(fcm_token);
        dest.writeString(photo_uri);
        dest.writeString(phone_no);
        dest.writeString(vehicle_type);
        dest.writeString(vehicle_name);
        dest.writeString(vehicle_no);
        dest.writeString(trip_type);
        dest.writeString(pick_location);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(drop_location);
        dest.writeInt(seating_capacity);
        dest.writeInt(remaining_seats);
        dest.writeInt(charges_per_km);
        dest.writeString(route);
        dest.writeInt(male);
        dest.writeInt(female);
        dest.writeInt(kids);
    }


}
