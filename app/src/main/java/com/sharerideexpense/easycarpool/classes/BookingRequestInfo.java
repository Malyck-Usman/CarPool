package com.sharerideexpense.easycarpool.classes;

import com.google.firebase.firestore.GeoPoint;

public class BookingRequestInfo {
    private String request_id;
    private String driver_id;
    private String passenger_id;
    private String passenger_name;
    private String passenger_phone_no;
    private String trip_id;
    private String req_pick_location;
    private String req_drop_location;
    private GeoPoint starting_point;
    private GeoPoint ending_point;
    private int seats_booked;
    private int status;
    private int male;
    private int female;
    private int kids;
    private boolean active;
    private String date;
    private String charges_range;

    public BookingRequestInfo() {
    }

    public BookingRequestInfo(String driver_id, String passenger_id, String passenger_name, String passenger_phone_no, String trip_id, String req_pick_location, String req_drop_location, GeoPoint starting_point, GeoPoint ending_point, int seats_booked, int status, int male, int female, int kids, boolean active, String date, String charges_range) {
        this.driver_id = driver_id;
        this.passenger_id = passenger_id;
        this.passenger_name = passenger_name;
        this.passenger_phone_no = passenger_phone_no;
        this.trip_id = trip_id;
        this.req_pick_location = req_pick_location;
        this.req_drop_location = req_drop_location;
        this.starting_point = starting_point;
        this.ending_point = ending_point;
        this.seats_booked = seats_booked;
        this.status = status;
        this.male = male;
        this.female = female;
        this.kids = kids;
        this.active = active;
        this.date = date;
        this.charges_range = charges_range;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPassenger_phone_no() {
        return passenger_phone_no;
    }

    public void setPassenger_phone_no(String passenger_phone_no) {
        this.passenger_phone_no = passenger_phone_no;
    }

    public String getCharges_range() {
        return charges_range;
    }

    public void setCharges_range(String charges_range) {
        this.charges_range = charges_range;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
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

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public GeoPoint getStarting_point() {
        return starting_point;
    }

    public void setStarting_point(GeoPoint starting_point) {
        this.starting_point = starting_point;
    }

    public GeoPoint getEnding_point() {
        return ending_point;
    }

    public void setEnding_point(GeoPoint ending_point) {
        this.ending_point = ending_point;
    }

    public int getSeats_booked() {
        return seats_booked;
    }

    public void setSeats_booked(int seats_booked) {
        this.seats_booked = seats_booked;
    }

    public int getStatus() {
        return status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReq_pick_location() {
        return req_pick_location;
    }

    public void setReq_pick_location(String req_pick_location) {
        this.req_pick_location = req_pick_location;
    }

    public String getReq_drop_location() {
        return req_drop_location;
    }

    public void setReq_drop_location(String req_drop_location) {
        this.req_drop_location = req_drop_location;
    }
}
