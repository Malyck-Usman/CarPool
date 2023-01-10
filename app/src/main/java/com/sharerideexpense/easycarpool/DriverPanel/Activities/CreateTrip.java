package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.Adaptors.AddressAdapter;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.DirectionPoints;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;


import java.io.IOException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CreateTrip extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final int GET_START_POINT = 2;
    private static final int GET_END_POINT = 3;
    private static final int GPS_REQUEST_CODE = 5;
    ImageView iv_select_location;
    MaterialTimePicker timePicker;
    private MaterialDatePicker datePicker;
    EditText edt_trip_name, edt_seats, edt_charges_per_km;
    TextView tv_pick_up, tv_drop_off, tv_departure_time, tv_departure_date;
    RadioGroup rg_trip_type;
    RadioButton rb_daily, rb_one_time;
    CardView btn_create;
    RelativeLayout rl_location;
    boolean update_required = true;
    double src_lat = 0, src_lng = 0, dest_lat = 0, dest_lng = 0;
    String src_address, dest_address, user_id, src_locality, src_sub_locality;
    boolean dest = false;
    List<Address> str;
    AppCompatButton btn_cancel_dialog, btn_confirm_dialog;
    Dialog mapDialog;
    AddressAdapter addressAdapter;
    ArrayList<Address> list_address;
    String value;
    FirebaseFirestore mDBRef;
    long fuel_price;
    int per_km_charge, selected_charge = 0;

    //    GoogleMap mGoogleMap;
    ArrayList markerPoints = new ArrayList();
    String routePolyLine;
    FirebaseMessaging mFCM;
    String v_no, v_name, v_type;
    MySharedPreferences prefs;
    String trip_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        Init();
        DateTimePickerInit();
    }

//    public void  UpdateFuelPrice() {
//        DatabaseReference mDBRef = FirebaseDatabase.getInstance().getReference();
//
//        mDBRef.child(getString(R.string.FUEL_PRICE)).get()
//                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                    @Override
//                    public void onSuccess(DataSnapshot dataSnapshot) {
//                        Log.d("TAG", "data snapshot " + dataSnapshot);
//                        if(dataSnapshot!=null)
//                        fuel_price= (long) dataSnapshot.child(getString(R.string.PRICE)).getValue();
//
//                        UpdatePricing();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("TAG", "Failed to load api keys "+e.getMessage());
//                    }
//                });
//    }
//    private void {
//        mDBRef.collection("fuel").document("fuelPrice").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                fuel_price = Float.parseFloat(Objects.requireNonNull(documentSnapshot.get("price")).toString());
//                UpdatePricing();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("TAG", "Cant get fuel price " + e.getMessage());
//                Toast.makeText(CreateTrip.this, "Failed to fetch data, Check your Internet and try again", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void DateTimePickerInit() {
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build();
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = getTime(timePicker.getHour(), timePicker.getMinute());
//                String time=timePicker.getHour()+":"+timePicker.getMinute();
                tv_departure_time.setText(time);
            }
        });
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build();
        materialDateBuilder.setCalendarConstraints(constraintsBuilder);
        materialDateBuilder.setTitleText("SELECT A DATE");
        datePicker = materialDateBuilder.build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utc.setTimeInMillis((Long) selection);
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                String date = format.format(utc.getTime());
                tv_departure_date.setText(date);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_select_location:
                if (isGPSEnabled()) {
                    ShowInterstitialAd();
                    Intent i = new Intent(this, ChooseLocationActivity.class);
                    startActivityForResult(i, GET_START_POINT);
                }
                break;
            case R.id.tv_select_time:
                timePicker.show(getSupportFragmentManager(), "DepartureTime");
                break;
            case R.id.tv_select_date:
                datePicker.show(getSupportFragmentManager(), "Departure_date");
                break;
//            case R.id.btn_cancel_location_dialog:
//                mapDialog.cancel();
////                mapView.invalidate();
//                break;
            case R.id.btn_create_trip:
                if (prefs.getPlacesApiKey().equals("") || prefs.getFcmServerKey().equals("")) {
                    Toast.makeText(this, "Check Your Internet Connection and try again", Toast.LENGTH_SHORT).show();
                    getKeys();
                } else {
                    CreateClicked();
                }
                break;
            case R.id.rl_location_text:
                iv_select_location.performClick();
                break;
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabeled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabeled) {
            return true;
        } else {
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this)
                    .setTitle("GPS not enabled")
                    .setMessage("Gps is required for this app to work,please enable GPS")
                    .setPositiveButton("yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void CreateClicked() {
        InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        if (CheckRoute() && CheckName() && getVehicleInfo() && CheckPickUp() && CheckDropOff() && CheckDepartureTime() && CheckDepartureDate() && CheckSeatingCapacity() && CheckCharges() && CheckTripType()) {
            TripInfo trip = new TripInfo(
                    user_id
                    , prefs.getUserName()
                    , prefs.getUserPhoneNo()
                    , prefs.getUserOnlineImageUri()
                    , v_name
                    , edt_trip_name.getText().toString()
                    , trip_type
                    , v_type
                    , v_no
                    , tv_pick_up.getText().toString().trim()
                    , new GeoPoint(src_lat, src_lng)
                    , src_locality
                    , src_sub_locality
                    , tv_departure_date.getText().toString()
                    , tv_departure_time.getText().toString().trim()
                    , tv_drop_off.getText().toString().trim()
                    , new GeoPoint(dest_lat, dest_lng)
                    , Integer.parseInt(edt_seats.getText().toString().trim())
                    , Integer.parseInt(edt_seats.getText().toString().trim())
                    , edt_charges_per_km.getText().toString().trim()
                    , routePolyLine
                    , 0, 0, 0
            );
            mDBRef.collection(getString(R.string.TRIPS)).add(trip).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    startActivity(new Intent(CreateTrip.this, MyRides.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateTrip.this, "Some thing went wrong, Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(CreateTrip.this, "Please complete all the fields", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean CheckCharges() {
        if (!edt_charges_per_km.getText().toString().equals("")) {
            int charges = Integer.parseInt(edt_charges_per_km.getText().toString().trim());
            if (charges < 5 || charges > 15) {
                edt_charges_per_km.setError("Charges should be between 5-15 Rupees");
            } else {
                edt_charges_per_km.setError(null);
                return true;
            }
        } else {
            edt_charges_per_km.setError("Required");
        }
        return false;
    }

    private boolean CheckTripType() {
        return !trip_type.equals("");
    }

    private boolean CheckSeatingCapacity() {
        if (edt_seats.getText().toString().equals("")) {
            edt_seats.setError("Required");
            return false;
        } else {
            edt_seats.setError(null);
            return true;
        }
    }

    private boolean CheckDepartureDate() {
        if(trip_type.equals(getString(R.string.ONE_TIME))){
            if (tv_departure_date.getText().toString().equals("Date")) {
            tv_departure_date.setError("Required");
            return false;
        } else {
            tv_departure_date.setError(null);
            return true;
        }
        }
        return true;
    }

    private boolean CheckDepartureTime() {
        if (tv_departure_time.getText().toString().equals("Time")) {
            tv_departure_time.setError("Required");
            return false;
        } else {
            tv_departure_time.setError(null);
            return true;
        }
    }

    private boolean CheckDropOff() {
        if (tv_drop_off.getText().toString().equals("")) {
            tv_drop_off.setError("required");
            return false;
        } else {
            tv_drop_off.setError(null);
            return true;
        }
    }

    private boolean CheckRoute() {
        if (src_lat == 0.0 && src_lng == 0 && dest_lat == 0 && dest_lng == 0) {
            Toast.makeText(this, "Please select the route to create trip", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean CheckPickUp() {
        if (tv_pick_up.getText().toString().equals("")) {
            tv_pick_up.setError("required");
            return false;
        } else {
            tv_pick_up.setError(null);
            return true;
        }
    }

    private boolean getVehicleInfo() {

        v_no = prefs.getVehicleNo();
        v_name = prefs.getVehicleName();
        v_type = prefs.getVehicleType();
        return !v_no.equals("") && !v_type.equals("") && !v_name.equals("");

    }

    private boolean CheckName() {
        if (edt_trip_name.getText().toString().equals("")) {
            edt_trip_name.setError("Required");
            return false;
        } else {
            edt_trip_name.setError(null);
            return true;
        }
    }


    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_daily:
                trip_type = getString(R.string.DAILY);
                tv_departure_date.setVisibility(View.GONE);
                break;
            case R.id.rb_one_time:
                trip_type = getString(R.string.ONE_TIME);
                tv_departure_date.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void Init() {
        setToolbar();
        prefs = new MySharedPreferences(this);
        edt_trip_name = findViewById(R.id.edt_trip_name);
        edt_seats = findViewById(R.id.tv_seating_capacity_ride_detail);

        tv_pick_up = findViewById(R.id.tv_start_point);
        tv_drop_off = findViewById(R.id.tv_drop_off);
        tv_departure_date = findViewById(R.id.tv_select_date);
        tv_departure_time = findViewById(R.id.tv_select_time);
        tv_departure_date.setOnClickListener(this);
        tv_departure_time.setOnClickListener(this);
        edt_charges_per_km = findViewById(R.id.edt_charges_per_km);
        rg_trip_type = findViewById(R.id.rg_trip_type);
        rg_trip_type.setOnCheckedChangeListener(this);
        rl_location = findViewById(R.id.rl_location_text);
        rl_location.setOnClickListener(this);
        rb_daily = findViewById(R.id.rb_daily);
        rb_one_time = findViewById(R.id.rb_one_time);
        btn_create = findViewById(R.id.btn_create_trip);
        btn_create.setOnClickListener(this);
        iv_select_location = findViewById(R.id.iv_select_location);
        iv_select_location.setOnClickListener(this);
        mDBRef = FirebaseFirestore.getInstance();
//        UpdateFuelPrice();
        CheckForUpdate();
        if (!prefs.getUserId().equals("")) {
            user_id = prefs.getUserId();
            //move forward
        } else {
            Toast.makeText(this, "Sign in to continue", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, Signin.class));
        }
    }

    private void CheckForUpdate() {
        mDBRef.collection(getString(R.string.UPDATE_REQUIRED)).document(getString(R.string.UPDATE_REQUIRED)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        update_required = Boolean.TRUE.equals(documentSnapshot.getBoolean(getString(R.string.REQUIRED)));
                    }
                });
    }

//    private void UpdatePricing() {
//        if (prefs.getVehicleType().equals(getString(R.string.BIKE))) {
//            per_km_charge = (int) ((fuel_price) / (44 * 2));
//        } else if (prefs.getVehicleType().equals(getString(R.string.CAR))) {
//            per_km_charge = (int) ((fuel_price) / (12 * 4));
//        } else if (prefs.getVehicleType().equals(getString(R.string.SUV))) {
//            per_km_charge = (int) ((fuel_price) / (10 * 4));
//        }
//        ((RadioButton) rg_rate.getChildAt(0)).setText(String.valueOf(per_km_charge - 1));
//        ((RadioButton) rg_rate.getChildAt(1)).setText(String.valueOf(per_km_charge));
//        ((RadioButton) rg_rate.getChildAt(2)).setText(String.valueOf(per_km_charge + 1));
//
//    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ImageView btn_toolbar = toolbar.findViewById(R.id.iv_toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Create Trip");
        btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            DirectionPoints dp = data.getExtras().getParcelable(getString(R.string.ROUTE));
            ArrayList<LatLng> route = dp.getLatLng();
            LatLng src = route.get(0);
            LatLng dest = route.get(route.size() - 1);
            src_lat = src.latitude;
            src_lng = src.longitude;
            dest_lng = dest.longitude;
            dest_lat = dest.latitude;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            LatLng latLng = new LatLng(src_lat, src_lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f);
            try {
                addresses = geocoder.getFromLocation(src_lat, src_lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                src_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                src_locality = addresses.get(0).getLocality();
                src_sub_locality = addresses.get(0).getSubLocality();
                tv_pick_up.setText(src_address);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            addMarker(new LatLng(dest_lat, dest_lng));
            Geocoder destGeocoder;
            List<Address> destAddresses;
            destGeocoder = new Geocoder(this, Locale.getDefault());

            try {
                destAddresses = destGeocoder.getFromLocation(dest_lat, dest_lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                dest_address = destAddresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                tv_drop_off.setText(dest_address);
            } catch (IOException e) {
                e.printStackTrace();
            }
            routePolyLine = PolyUtil.encode(route);
        }
    }

    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }

    public void getKeys() {
        DatabaseReference mDBRef = FirebaseDatabase.getInstance().getReference();

        mDBRef.child(getString(R.string.API_KEYS)).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Log.d("TAG", "data snapshot " + dataSnapshot);
                        prefs.setPlacesApiKey((String) dataSnapshot.child(getString(R.string.PLACES_API_KEY)).getValue());
                        prefs.setFcmSeverKey((String) dataSnapshot.child(getString(R.string.FCM_KEY)).getValue());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed to load api keys " + e.getMessage());
                    }
                });
    }

}