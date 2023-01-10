package com.sharerideexpense.easycarpool.UserPanel.Interface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sharerideexpense.easycarpool.UserPanel.Classes.TripRequestsData;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TripRequestDao {
    @Query("Select * from TripRequestsData")
    List<TripRequestsData> getAll();

    @Query("Select trip_id from TripRequestsData")
    List<String> getTripIds();

    @Query("Select * from TripRequestsData where trip_id=:tripId")
   TripRequestsData getTripObject(String tripId);

    @Insert
    void insertAll(TripRequestsData tripRequestsData);

    @Insert
    void insertList(ArrayList<TripRequestsData> tripRequestsData);


    @Delete
    void delete(TripRequestsData tripRequestsData);

}
