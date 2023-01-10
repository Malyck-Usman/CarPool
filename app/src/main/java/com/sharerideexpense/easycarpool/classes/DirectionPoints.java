package com.sharerideexpense.easycarpool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DirectionPoints implements Parcelable {
   ArrayList<LatLng> latLng;

    public DirectionPoints() {
    }

    protected DirectionPoints(Parcel in) {
        latLng = in.createTypedArrayList(LatLng.CREATOR);
    }

    public static final Creator<DirectionPoints> CREATOR = new Creator<DirectionPoints>() {
        @Override
        public DirectionPoints createFromParcel(Parcel in) {
            return new DirectionPoints(in);
        }

        @Override
        public DirectionPoints[] newArray(int size) {
            return new DirectionPoints[size];
        }
    };

    public ArrayList<LatLng> getLatLng() {
        return latLng;
    }

    public void setLatLng(ArrayList<LatLng> latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(latLng);
    }
}
