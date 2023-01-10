package com.sharerideexpense.easycarpool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.Comparator;

public class TripInfo implements Parcelable, Comparable {
    private String driver_id;
    private String driver_name;
    private String driver_phone_no;
    private String photo_uri;
    private String vehicle_name;
    private String trip_id;
    private String trip_name;
    private String trip_type;
    private String vehicle_type;
    private String vehicle_no;
    private String pick_location;
    private GeoPoint pick_point;
    private String pick_locality;
    private String pick_sub_locality;
    private String date;
    private String time;
    private String drop_location;
    private GeoPoint drop_point;
    private int seating_capacity;
    private int remaining_seats;
    private String charges_per_km;
    private String route;
    private int male;
    private int female;
    private int kids;
    private float distance;

    public TripInfo() {
    }


    public TripInfo(String driver_id, String driver_name, String driver_phone_no, String photo_uri, String vehicle_name, String trip_name, String trip_type, String vehicle_type, String vehicle_no, String pick_location, GeoPoint pick_point, String pick_locality, String pick_sub_locality, String date, String time, String drop_location, GeoPoint drop_point, int seating_capacity, int remaining_seats, String charges_per_km, String route, int male, int female, int kids) {
        this.driver_id = driver_id;
        this.driver_name = driver_name;
        this.driver_phone_no = driver_phone_no;
        this.photo_uri = photo_uri;
        this.vehicle_name = vehicle_name;
        this.trip_name = trip_name;
        this.trip_type = trip_type;
        this.vehicle_type = vehicle_type;
        this.vehicle_no = vehicle_no;
        this.pick_location = pick_location;
        this.pick_point = pick_point;
        this.pick_locality = pick_locality;
        this.pick_sub_locality = pick_sub_locality;
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


    protected TripInfo(Parcel in) {
        driver_id = in.readString();
        driver_name = in.readString();
        driver_phone_no = in.readString();
        photo_uri = in.readString();
        vehicle_name = in.readString();
        trip_id = in.readString();
        trip_name = in.readString();
        trip_type = in.readString();
        vehicle_type = in.readString();
        vehicle_no = in.readString();
        pick_location = in.readString();
        pick_locality = in.readString();
        pick_sub_locality = in.readString();
        date = in.readString();
        time = in.readString();
        drop_location = in.readString();
        seating_capacity = in.readInt();
        remaining_seats = in.readInt();
        charges_per_km = in.readString();
        route = in.readString();
        male = in.readInt();
        female = in.readInt();
        kids = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(driver_id);
        dest.writeString(driver_name);
        dest.writeString(driver_phone_no);
        dest.writeString(photo_uri);
        dest.writeString(vehicle_name);
        dest.writeString(trip_id);
        dest.writeString(trip_name);
        dest.writeString(trip_type);
        dest.writeString(vehicle_type);
        dest.writeString(vehicle_no);
        dest.writeString(pick_location);
        dest.writeString(pick_locality);
        dest.writeString(pick_sub_locality);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(drop_location);
        dest.writeInt(seating_capacity);
        dest.writeInt(remaining_seats);
        dest.writeString(charges_per_km);
        dest.writeString(route);
        dest.writeInt(male);
        dest.writeInt(female);
        dest.writeInt(kids);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripInfo> CREATOR = new Creator<TripInfo>() {
        @Override
        public TripInfo createFromParcel(Parcel in) {
            return new TripInfo(in);
        }

        @Override
        public TripInfo[] newArray(int size) {
            return new TripInfo[size];
        }
    };


    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
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

    public String getPick_locality() {
        return pick_locality;
    }

    public void setPick_locality(String pick_locality) {
        this.pick_locality = pick_locality;
    }

    public String getPick_sub_locality() {
        return pick_sub_locality;
    }

    public void setPick_sub_locality(String pick_sub_locality) {
        this.pick_sub_locality = pick_sub_locality;
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

    public String getCharges_per_km() {
        return charges_per_km;
    }

    public void setCharges_per_km(String charges_per_km) {
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


    public String getDriver_phone_no() {
        return driver_phone_no;
    }

    public void setDriver_phone_no(String driver_phone_no) {
        this.driver_phone_no = driver_phone_no;
    }

    @Override
    public int compareTo(Object o) {
        float compareTo=((TripInfo )o).distance;
        /* For Ascending order*/
        return (int) (this.distance-compareTo);
    }

}
