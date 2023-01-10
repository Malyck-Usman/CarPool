package com.sharerideexpense.easycarpool.UserPanel.Activities;


import static com.sharerideexpense.easycarpool.Activities.ApplicationController.googleSignInClient;
import static com.sharerideexpense.easycarpool.Activities.ApplicationController.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.GeoPoint;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Activities.PrivacyPolicy;
import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.Adaptors.PlacesAdaptor;
import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.sharerideexpense.easycarpool.DriverPanel.Activities.MyRides;
import com.sharerideexpense.easycarpool.Interface.OnPlaceClick;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Adaptors.RideSearchAdaptor;
import com.sharerideexpense.easycarpool.UserPanel.Classes.CarPoolDb;
import com.sharerideexpense.easycarpool.UserPanel.Interface.TripRequestDao;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.PlaceInfo;
import com.sharerideexpense.easycarpool.classes.TripDetailMerged;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearchActivity extends AppCompatActivity implements View.OnClickListener, AppOpen, OnPlaceClick, TextWatcher, TabLayout.OnTabSelectedListener {
    private static final int GPS_REQUEST_CODE = 2;
    private Skeleton skeleton, search_skeleton;
    private FirebaseFirestore mDBRef;
    private ArrayList<TripInfo> tripList = new ArrayList<>();
    private ArrayList<TripInfo> carsList = new ArrayList<>();
    private ArrayList<TripInfo> bikesList = new ArrayList<>();
    private ArrayList<TripInfo> suvList = new ArrayList<>();
    private RideSearchAdaptor mAdaptor;
    private RecyclerView rv_rideSearch;
    private DrawerLayout drawerLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int LOCATION_REQUEST_CODE = 1;
    private View headerView;
    MySharedPreferences prefs;
    private Timer timer;
    //    private SwipeRefreshLayout SwipeRefresh;
    private View navigationDrawerLayout;
    String locality = null;
    FrameLayout fl_ad;
    View margin;
    LinearLayout ll_no_ride;
    ApplicationController mController;
    CarPoolDb db;
    RecyclerView rv_search;
    ArrayList<PlaceInfo> mAddressList;
    PlacesAdaptor mPlacesAdaptor;
    String countryCode = "";
    GeoPoint currentLoc;
    EditText edt_search;
    ImageView iv_clear;
    TabLayout tl_vehicle_type;
    int vehicleTypePosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        Init();
    }

    private void Init() {
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);
        setTopBar();
        db = Room.databaseBuilder(getApplicationContext(), CarPoolDb.class, getString(R.string.CARPOOL_DB)).allowMainThreadQueries().build();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDBRef = FirebaseFirestore.getInstance();
        rv_search = findViewById(R.id.rv_user_search_filter);
        mAddressList = new ArrayList<>();
        rv_search.setLayoutManager(new LinearLayoutManager(this));
        mPlacesAdaptor = new PlacesAdaptor(mAddressList, UserSearchActivity.this, this);
        rv_search.setAdapter(mPlacesAdaptor);
        search_skeleton = SkeletonLayoutUtils.applySkeleton(rv_search, R.layout.item_address);
        mAdaptor = new RideSearchAdaptor(this, carsList);
        rv_rideSearch = findViewById(R.id.rv_search_ride);
        rv_rideSearch.setLayoutManager(new LinearLayoutManager(this));
        rv_rideSearch.setAdapter(mAdaptor);
        skeleton = SkeletonLayoutUtils.applySkeleton(rv_rideSearch, R.layout.item_user_search_ride);
        ll_no_ride = findViewById(R.id.ll_no_ride);
        tl_vehicle_type = findViewById(R.id.tl_vehicle_type);
        tl_vehicle_type.addOnTabSelectedListener(this);
//        SwipeRefresh = findViewById(R.id.swipe_refresh);
//        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (locality != null && currentLoc != null) {
//
//                    RunQuery(locality, currentLoc);
//                } else {
//                    if (isGPSEnabled()) {
//                        GetCurrentLocation();
//                    }
//                }
//            }
//        });
        skeleton.showSkeleton();
        ShowUserData();
        CheckPermission();

    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);

        } else {
            if (isGPSEnabled()) {
                GetCurrentLocation();
            }

            // already permission granted
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

    private void setTopBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        TextView tv_title = findViewById(R.id.tv_toolbar_main);
        tv_title.setText("Find A Ride");
        RelativeLayout rl_search = toolbar.findViewById(R.id.rl_search_tb);
        LinearLayout ll_search_normal = toolbar.findViewById(R.id.ll_search_tb);
        ll_search_normal.setVisibility(View.VISIBLE);
        LinearLayout ll_normal = toolbar.findViewById(R.id.ll_normal_tb);
        edt_search = toolbar.findViewById(R.id.edt_search_tb);
        edt_search.addTextChangedListener(this);
        ll_search_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_normal.setVisibility(View.GONE);
                rl_search.setVisibility(View.VISIBLE);

            }
        });
        ImageView iv_back = toolbar.findViewById(R.id.iv_back_tb);
        iv_clear = toolbar.findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_search.getText().toString().length() > 0) {
                    edt_search.setText("");
                }
                iv_clear.setVisibility(View.GONE);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_search.setVisibility(View.GONE);
                ll_normal.setVisibility(View.VISIBLE);
                edt_search.setText("");
                if (locality != null && currentLoc != null) {
                    SortAndUpdateResult(currentLoc);
                }
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationDrawerLayout = findViewById(R.id.drawer_menu_passenger);
    }


    private void RunQuery(String locality, GeoPoint currentLoc) {
        tripList.clear();
//        userList.clear();
//        tripDetailList.clear();
        mDBRef.collection(getString(R.string.TRIPS)).whereEqualTo(getString(R.string.PICK_LOCALITY), locality).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                TripRequestDao tripRequestDao = db.tripRequestDao();
                List<String> tripRequestIds = tripRequestDao.getTripIds();
                String id = prefs.getUserId();
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    TripInfo info = qds.toObject(TripInfo.class);
                    if (info.getRemaining_seats() > 0 && (!info.getDriver_id().equals(id))) {
                        if (tripRequestIds.size() > 0) {
                            if (!(tripRequestIds.contains(qds.getId()))) {
                                info.setTrip_id(qds.getId());
                                tripList.add(info);
                            }
                        } else {
                            info.setTrip_id(qds.getId());
                            tripList.add(info);
                        }
                    }
                }
                if (tripList.size() > 0) {
                    SortAndUpdateResult(currentLoc);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error " + e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ll_no_ride.setVisibility(View.VISIBLE);
//                SwipeRefresh.setRefreshing(false);
                skeleton.showOriginal();
                if (tripList.size() != 0) {
                    ll_no_ride.setVisibility(View.GONE);
                    mAdaptor.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetCurrentLocation();
            } else {
                isGPSEnabled();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void GetCurrentLocation() {
        mFusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {

                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Location loc = location;
                if (loc != null) {
                    Geocoder geocoder = new Geocoder(UserSearchActivity.this, Locale.ENGLISH);
                    List<Address> addresses = new ArrayList<Address>();
                    try {
                        addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 3);
                        if (addresses.size() != 0) {
                            locality = addresses.get(0).getLocality();
                            countryCode = prefs.getCountryCode();
                            if (countryCode.equals("")) {
                                try {
                                    getCountryCode(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            currentLoc = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                            RunQuery(locality, currentLoc);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                Log.d("TAG","Location "+location);
//                Log.d("TAG","Location "+location.getLongitude());
                }
            }
        });

    }

    private String getCountryCode(LatLng latlng) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        if (addresses != null && addresses.size() > 0) {
            countryCode = addresses.get(0).getCountryCode();
            prefs.setCountryCode(countryCode);
            return countryCode;
        }
        return "";
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
//            super.onBackPressed();
        }
    }

    private void ShowUserData() {
        prefs = new MySharedPreferences(this);
        TextView userName = (TextView) navigationDrawerLayout.findViewById(R.id.tv_drawer_user_name);
        TextView userEmail = (TextView) navigationDrawerLayout.findViewById(R.id.tv_drawer_email);
        CircleImageView iv_user = (CircleImageView) navigationDrawerLayout.findViewById(R.id.iv_drawer_user);
        userName.setText(prefs.getUserName());
        userEmail.setText(prefs.getUserEmail());
        if (!prefs.getUserImageUri().equals(""))
            iv_user.setImageURI(Uri.parse(prefs.getUserImageUri()));
        TextView tv_home = navigationDrawerLayout.findViewById(R.id.tv_drawer_home);
        TextView tv_my_bookings = navigationDrawerLayout.findViewById(R.id.tv_drawer_my_bookings);
        TextView tv_logout = navigationDrawerLayout.findViewById(R.id.tv_drawer_logout);
        TextView tv_privacy_policy = navigationDrawerLayout.findViewById(R.id.tv_drawer_privacy_policy);
        CardView cv_rider_mode = navigationDrawerLayout.findViewById(R.id.cv_drawer_rider_mode);
        tv_home.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);
        tv_my_bookings.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        cv_rider_mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
                ShowInterstitialAd();
        switch (v.getId()) {
            case R.id.tv_drawer_home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_drawer_my_bookings:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(UserSearchActivity.this, BookingStatus.class));
                break;
            case R.id.tv_drawer_privacy_policy:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(UserSearchActivity.this, PrivacyPolicy.class));
                break;
            case R.id.tv_drawer_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                mAuth.signOut();
                prefs.clearData();
                if (googleSignInClient != null)
                    googleSignInClient.signOut();
                startActivity(new Intent(this, Signin.class));
                finish();
                break;
            case R.id.cv_drawer_rider_mode:
                drawerLayout.closeDrawer(GravityCompat.START);
                prefs.setUserMode(getString(R.string.RIDER_MODE));
                startActivity(new Intent(UserSearchActivity.this, MyRides.class));
                finish();
                break;
        }
    }

    @Override
    public void restoreAds() {
        fl_ad.setVisibility(View.VISIBLE);
        margin.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeAds() {
        fl_ad.setVisibility(View.INVISIBLE);
        margin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void placeClicked(PlaceInfo placeInfo) {
        edt_search.setText(placeInfo.getName());
        skeleton.showSkeleton();
        rv_search.setVisibility(View.GONE);
        HideKeyboard();
        GeoPoint geoPoint;
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(placeInfo.getName(), 4);
            if (addresses.size() > 0) {
                geoPoint = new GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                SortAndUpdateResult(geoPoint);
            }
        } catch (Exception e) {
            Log.d("TAG", "Failed to get lng lat " + e.getMessage());
        }
    }

    private void SortAndUpdateResult(GeoPoint geoPoint) {
        bikesList.clear();
        carsList.clear();
        suvList.clear();
        Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                if (tripList != null && tripList.size() > 0) {
                    for (TripInfo trip : tripList) {
                        float[] length = new float[2];
                        Location.distanceBetween(
                                trip.getPick_point().getLatitude()
                                , trip.getPick_point().getLongitude()
                                , geoPoint.getLatitude(), geoPoint.getLongitude(), length);
                        float distance = length[0] / 1000;
                        trip.setDistance(distance);
//                        Log.d("TAG", "Distance before sorting " + trip.getDistance());
                    }
                    /////////
                    for (int i = 0; i < tripList.size() - 1; i++) {
                        int index = i;
                        for (int j = i + 1; j < tripList.size(); j++) {
                            if (tripList.get(j).getDistance() < tripList.get(index).getDistance()) {
                                index = j;//searching for lowest index
                            }
                        }
                        TripInfo smallerValue = tripList.get(index);
//                tripListSorted.add(smallerValue);
//                        Log.d("TAG", "Smallest value is " + smallerValue.getDistance());
                        tripList.set(index, tripList.get(i));
                        tripList.set(i, smallerValue);
                    }
                    ////////////////
                }
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("TAG", "Handler called............... ");
                        String vehicleType = null;
                        for (int index = 0; index < tripList.size() - 1; index++) {
                            vehicleType = tripList.get(index).getVehicle_type();
                            if (vehicleType.equals(getString(R.string.CAR))) {
                                carsList.add(tripList.get(index));
                            } else if (vehicleType.equals(getString(R.string.BIKE))) {
                                bikesList.add(tripList.get(index));
                            } else if (vehicleType.equals(getString(R.string.SUV))) {
                                suvList.add(tripList.get(index));
                            }
                        }
                        UpdateRideList(vehicleTypePosition);

                    }
                });
            }

        }).start();


        /////
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rv_search.getWindowToken(), 0);
    }

    private void getPredictions(Editable s) {
        mAddressList.clear();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        String key = new MySharedPreferences(this).getPlacesApiKey();
        Request request;
        if (countryCode.equals("")) {
            request = new Request.Builder()
                    //places    &limit=4    &types=geocode
                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&limit=6&language=en&key=" + key)
                    .build();
        } else {
            request = new Request.Builder()
                    //places    &limit=4    &types=geocode
                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&components=country:" + countryCode + "&limit=6&language=en&key=" + key)
                    .build();

        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("TAG", "FAiled to load data" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    PlaceInfo placeInfo;
                    String responseData = response.body().string();
                    JSONObject obj = new JSONObject(responseData);
                    String status = obj.getString("status");
                    if (status.equals("INVALID_REQUEST")) {
                        countryCode = "";
                        getPredictions(s);
                    }
                    JSONArray places = obj.getJSONArray("predictions");
                    for (int pos = 0; pos < places.length(); pos++) {
                        JSONObject array1 = places.getJSONObject(pos);
                        String placeName = array1.getString("description");
                        Log.d("TAG", "Place name " + placeName);
                        placeInfo = new PlaceInfo();
                        placeInfo.setName(placeName);
                        mAddressList.add(placeInfo);
//                            }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mAddressList.size() > 0)
                                mPlacesAdaptor.notifyDataSetChanged();
                            search_skeleton.showOriginal();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 1) {
            rv_search.setVisibility(View.VISIBLE);
            search_skeleton.showSkeleton();
            iv_clear.setVisibility(View.VISIBLE);
        }
        if (s.length() > 3) {
            getPredictions(s);

        } else if (s.length() == 0) {
            rv_search.setVisibility(View.GONE);
            iv_clear.setVisibility(View.GONE);

        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
            case 0:
                vehicleTypePosition = 0;
                UpdateRideList(0);
                break;
            case 1:
                vehicleTypePosition = 1;
                UpdateRideList(1);
                break;
            case 2:
                vehicleTypePosition = 2;
                UpdateRideList(2);
                break;
        }
        ShowInterstitialAd();
    }
    private void UpdateRideList(int i) {
        if (i == 0) {
            mAdaptor = new RideSearchAdaptor(this, carsList);
            rv_rideSearch.setAdapter(mAdaptor);
        } else if (i == 1) {
            mAdaptor = new RideSearchAdaptor(this, bikesList);
            rv_rideSearch.setAdapter(mAdaptor);
        } else if (i == 2) {
            mAdaptor = new RideSearchAdaptor(this, suvList);
            rv_rideSearch.setAdapter(mAdaptor);
        }
        mAdaptor.notifyDataSetChanged();
//        skeleton.showOriginal();

    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }
}