package com.sharerideexpense.easycarpool.UserPanel.Classes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TripRequestsData {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "trip_id")
    public String TripId;
}
