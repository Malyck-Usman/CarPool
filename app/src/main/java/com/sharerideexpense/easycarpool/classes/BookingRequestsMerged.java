package com.sharerideexpense.easycarpool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BookingRequestsMerged implements Parcelable {

    private String request_id;
    private String driver_id;
    private String driver_phone_no;
    private String vehicle_no;
    private String passenger_id;
    private String passenger_name;
    private String passenger_phone_no;
    private String trip_id;
    private String vehicle_name;
    private String trip_name;
    private double req_start_lat;
    private double req_start_lng;
    private double req_end_lat;
    private double req_end_lng;
    private String route;
    private String date;
    private String time;
    private int male;
    private int female;
    private int kids;
    private int seats_booked;
    private int remaining_seats;
    private String pick_location;
    private String drop_location;
    private boolean active;
    private String charges_range;


    public BookingRequestsMerged() {
    }

    protected BookingRequestsMerged(Parcel in) {
        request_id = in.readString();
        driver_id = in.readString();
        driver_phone_no = in.readString();
        vehicle_no = in.readString();
        passenger_id = in.readString();
        passenger_name = in.readString();
        passenger_phone_no = in.readString();
        trip_id = in.readString();
        vehicle_name = in.readString();
        trip_name = in.readString();
        req_start_lat = in.readDouble();
        req_start_lng = in.readDouble();
        req_end_lat = in.readDouble();
        req_end_lng = in.readDouble();
        route = in.readString();
        date = in.readString();
        time = in.readString();
        male = in.readInt();
        female = in.readInt();
        kids = in.readInt();
        seats_booked = in.readInt();
        remaining_seats = in.readInt();
        pick_location = in.readString();
        drop_location = in.readString();
        active = in.readByte() != 0;
        charges_range = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(request_id);
        dest.writeString(driver_id);
        dest.writeString(driver_phone_no);
        dest.writeString(vehicle_no);
        dest.writeString(passenger_id);
        dest.writeString(passenger_name);
        dest.writeString(passenger_phone_no);
        dest.writeString(trip_id);
        dest.writeString(vehicle_name);
        dest.writeString(trip_name);
        dest.writeDouble(req_start_lat);
        dest.writeDouble(req_start_lng);
        dest.writeDouble(req_end_lat);
        dest.writeDouble(req_end_lng);
        dest.writeString(route);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeInt(male);
        dest.writeInt(female);
        dest.writeInt(kids);
        dest.writeInt(seats_booked);
        dest.writeInt(remaining_seats);
        dest.writeString(pick_location);
        dest.writeString(drop_location);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(charges_range);
    }

    public static final Creator<BookingRequestsMerged> CREATOR = new Creator<BookingRequestsMerged>() {
        @Override
        public BookingRequestsMerged createFromParcel(Parcel in) {
            return new BookingRequestsMerged(in);
        }

        @Override
        public BookingRequestsMerged[] newArray(int size) {
            return new BookingRequestsMerged[size];
        }
    };

    public String getCharges_range() {
        return charges_range;
    }

    public void setCharges_range(String charges_range) {
        this.charges_range = charges_range;
    }

    public String getPassenger_phone_no() {
        return passenger_phone_no;
    }

    public void setPassenger_phone_no(String passenger_phone_no) {
        this.passenger_phone_no = passenger_phone_no;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(String passenger_id) {
        this.passenger_id = passenger_id;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public double getReq_start_lat() {
        return req_start_lat;
    }

    public void setReq_start_lat(double req_start_lat) {
        this.req_start_lat = req_start_lat;
    }

    public double getReq_start_lng() {
        return req_start_lng;
    }

    public void setReq_start_lng(double req_start_lng) {
        this.req_start_lng = req_start_lng;
    }

    public double getReq_end_lat() {
        return req_end_lat;
    }

    public void setReq_end_lat(double req_end_lat) {
        this.req_end_lat = req_end_lat;
    }

    public double getReq_end_lng() {
        return req_end_lng;
    }

    public void setReq_end_lng(double req_end_lng) {
        this.req_end_lng = req_end_lng;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public int getSeats_booked() {
        return seats_booked;
    }

    public void setSeats_booked(int seats_booked) {
        this.seats_booked = seats_booked;
    }

    public int getRemaining_seats() {
        return remaining_seats;
    }

    public void setRemaining_seats(int remaining_seats) {
        this.remaining_seats = remaining_seats;
    }

    public String getPick_location() {
        return pick_location;
    }

    public void setPick_location(String pick_location) {
        this.pick_location = pick_location;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDriver_phone_no() {
        return driver_phone_no;
    }

    public void setDriver_phone_no(String driver_phone_no) {
        this.driver_phone_no = driver_phone_no;
    }

    @Override
    public int describeContents() {
        return 0;
    }



}
