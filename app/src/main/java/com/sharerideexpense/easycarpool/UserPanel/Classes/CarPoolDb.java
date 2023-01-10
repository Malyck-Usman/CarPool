package com.sharerideexpense.easycarpool.UserPanel.Classes;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sharerideexpense.easycarpool.UserPanel.Interface.TripRequestDao;

@Database(entities = {TripRequestsData.class},version = 1)
public abstract class CarPoolDb extends RoomDatabase {
    public abstract TripRequestDao tripRequestDao();
}
