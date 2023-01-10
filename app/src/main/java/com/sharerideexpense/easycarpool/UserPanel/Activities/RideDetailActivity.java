package com.sharerideexpense.easycarpool.UserPanel.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.maps.android.SphericalUtil;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Classes.CarPoolDb;
import com.sharerideexpense.easycarpool.UserPanel.Classes.TripRequestsData;
import com.sharerideexpense.easycarpool.UserPanel.Interface.TripRequestDao;
import com.sharerideexpense.easycarpool.classes.BookingRequestInfo;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.PolyUtil;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class RideDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private FusedLocationProviderClient mLocationClient;
    TextView tv_vehicle_name, tv_seating_capacity, tv_pick_up, tv_drop_off,
            tv_departure_time, tv_departure_date, tv_charges, tv_seats_available,
            tv_male, tv_phone_no, tv_vehicle_no, tv_female, tv_kids, tv_male_chip,
            tv_female_chip, tv_kid_chip, tv_btn_req_booking, tv_driver_name, tv_start_point_location, tv_end_point_location, tv_trip_type;
    GoogleMap mGoogleMap;
    ConstraintLayout cl_point_from, cl_point_to;
    RelativeLayout rl_btn_req_booking, rl_seats_booked, rl_departure_date;
    ConstraintLayout cl_map;
    private ArrayList<LatLng> route = new ArrayList<LatLng>();
    Polyline RoutePolyline;
    CircleImageView iv_current_location;
    Marker start_marker;
    Marker end_marker;
    CircleImageView iv_rider;
    boolean start_point = false;
    boolean end_point = false;
    LatLng start, end;
    int startIndex, endIndex;
    GeoPoint trip_start_point, trip_end_point;
    private int available_seats;
    ImageButton ib_plus_male, ib_minus_male, ib_plus_female, ib_minus_female, ib_plus_kids, ib_minus_kids;
    TripInfo tripInfo;
    private int seats = 0;
    private int male = 0;
    private int female = 0;
    private int kids = 0;
    private int charges = 0;
    CardView btn_request_booking;
    FrameLayout cv_content;
    FirebaseFirestore mDBRef;
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    ImageView iv_screen_control, btn_starting_point, btn_ending_point;
    boolean full_screen = false;
    ///////////////////////
    private ImageView mMarkerImageView, mMarkerImageViewRoute, mABMarkerImageView;
    private View mCustomMarkerView;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    Marker mCurrLocationMarker;
    Bitmap startMarkerIcon, endMarkerIcon, AMarkerIcon, BMarkerIcon;
    private View mMarkerView, mABMarkerView;
    private TextView mMarkerTextView, mABMarkerTextView;
    LatLng mCurrentGPSLocation;
    private String fcm_token = null;
    CarPoolDb carPoolDb;
    MySharedPreferences prefs;
    String chargeRange;


    //    private String serverKey=getResources().getString(R.string.FCM_SERVER_KEY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);
        Init();
    }

    @SuppressLint("MissingPermission")
    private void Init() {
        ///////////////
        ShowInterstitialAd();
        prefs = new MySharedPreferences(this);
        mMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_route_markers, null);
        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageViewRoute = (ImageView) mMarkerView.findViewById(R.id.iv_marker_image);
        mMarkerTextView = (TextView) mMarkerView.findViewById(R.id.tv_marker_name);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
        startMarkerIcon = getMarkerBitmapFromView2(mMarkerView, 1);
        endMarkerIcon = getMarkerBitmapFromView2(mMarkerView, 2);
        mABMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_a_b_markers, null);
        mABMarkerTextView = (TextView) mABMarkerView.findViewById(R.id.tv_marker_name);
        mABMarkerImageView = (ImageView) mABMarkerView.findViewById(R.id.iv_marker_image);
        AMarkerIcon = getMarkerBitmapForAB(mABMarkerView, 1);
        BMarkerIcon = getMarkerBitmapForAB(mABMarkerView, 2);
        //////////////
//        setToolbar();
        carPoolDb = Room.databaseBuilder(getApplicationContext(), CarPoolDb.class, getString(R.string.CARPOOL_DB)).build();

        mDBRef = FirebaseFirestore.getInstance();
        tv_start_point_location = findViewById(R.id.start_point_location);
        tv_end_point_location = findViewById(R.id.end_point_location);
        tv_driver_name = findViewById(R.id.tv_driver_name);
        iv_rider = findViewById(R.id.iv_driver_image);
        iv_current_location = findViewById(R.id.iv_current_location);
        iv_current_location.setOnClickListener(this);
        rl_btn_req_booking = findViewById(R.id.rl_btn_req_booking);
        rl_departure_date = findViewById(R.id.rl_Departure_date_ride_detail);
        tv_btn_req_booking = findViewById(R.id.tv_btn_req_booking);
        tv_phone_no = findViewById(R.id.tv_phone_no);
        tv_vehicle_no = findViewById(R.id.tv_vehicle_no);
        tv_trip_type = findViewById(R.id.tv_trip_type);
        tv_vehicle_name = findViewById(R.id.tv_vehicle_name);
        tv_seating_capacity = findViewById(R.id.tv_seating_capacity_ride_detail);
        tv_pick_up = findViewById(R.id.tv_start_point_ride_detail);
        tv_drop_off = findViewById(R.id.tv_drop_off_ride_detail);
        tv_departure_time = findViewById(R.id.tv_departure_time_ride_detail);
        tv_departure_date = findViewById(R.id.tv_my_departure_date_ride_detail);
        tv_charges = findViewById(R.id.tv_charges_ride_details);
        tv_seats_available = findViewById(R.id.tv_seats_available_ride_detail);
        btn_starting_point = findViewById(R.id.iv_start_point);
        btn_ending_point = findViewById(R.id.iv_end_point);
        tv_male = findViewById(R.id.tv_male_booking);
        tv_female = findViewById(R.id.tv_female_booking);
        tv_kids = findViewById(R.id.tv_kids_booking);
        btn_request_booking = findViewById(R.id.btn_request_booking_ride_detail);
        ib_minus_male = findViewById(R.id.btn_minus_male);
        ib_plus_male = findViewById(R.id.btn_plus_male);
        ib_plus_female = findViewById(R.id.btn_plus_female);
        ib_minus_female = findViewById(R.id.btn_minus_female);
        ib_minus_kids = findViewById(R.id.btn_minus_kids);
        ib_plus_kids = findViewById(R.id.btn_plus_kids);
        iv_screen_control = findViewById(R.id.iv_full_half_screen);
        cl_map = findViewById(R.id.cl_top_map);
        cv_content = findViewById(R.id.cv_search_ride);
        rl_seats_booked = findViewById(R.id.rl_seats_booked);
        iv_screen_control.setOnClickListener(this);
        btn_request_booking.setOnClickListener(this);
        btn_starting_point.setOnClickListener(this);
        btn_ending_point.setOnClickListener(this);
        ib_plus_male.setOnClickListener(this);
        ib_plus_female.setOnClickListener(this);
        ib_plus_kids.setOnClickListener(this);
        ib_minus_male.setOnClickListener(this);
        ib_minus_female.setOnClickListener(this);
        ib_minus_kids.setOnClickListener(this);
        tv_male_chip = findViewById(R.id.tv_male);
        tv_female_chip = findViewById(R.id.tv_female);
        tv_kid_chip = findViewById(R.id.tv_kid);
        cl_point_from = findViewById(R.id.cl_point_from);
        cl_point_to = findViewById(R.id.cl_point_to);
        cl_point_from.setOnClickListener(this);
        cl_point_to.setOnClickListener(this);

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            setData(intent);

        }
//        FrameLayout mvRideDetail=findViewById(R.id.mv_ride_detail);
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.mv_ride_detail, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this); //calls on map ready
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("TAG", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mCurrentLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                mCurrentGPSLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (mGoogleMap != null)
                    addCustomMarker(mCurrentGPSLocation);
                //Place current location marker

                //move map camera
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        }
    };

    private void setData(Intent intent) {
        tripInfo = intent.getExtras().getParcelable(getString(R.string.TRIP_INFO_MERGED));
        GetDriverInfo(tripInfo.getDriver_id());
        tv_vehicle_name.setText(tripInfo.getVehicle_name());
        tv_seating_capacity.setText(String.valueOf(tripInfo.getSeating_capacity()));
        tv_pick_up.setText(tripInfo.getPick_location());
        tv_drop_off.setText(tripInfo.getDrop_location());
        tv_departure_time.setText(tripInfo.getTime());
        if (tripInfo.getTrip_type().equals(getString(R.string.ONE_TIME))) {
            rl_departure_date.setVisibility(View.VISIBLE);
            tv_departure_date.setText(tripInfo.getDate());
        }
        tv_trip_type.setText(tripInfo.getTrip_type());
        charges= Integer.parseInt(tripInfo.getCharges_per_km());
        tv_charges.setText(tripInfo.getCharges_per_km());
        available_seats = tripInfo.getRemaining_seats();
        tv_seats_available.setText(String.valueOf(available_seats));
        route = (ArrayList<LatLng>) PolyUtil.decode(tripInfo.getRoute());
        double distance = SphericalUtil.computeLength(route) / 1000f;
        Log.d("TAG", "Route distance is " + distance);
        int male = tripInfo.getMale();
        int female = tripInfo.getFemale();
        int kids = tripInfo.getKids();
        if (male > 0 || female > 0 || kids > 0)
            rl_seats_booked.setVisibility(View.VISIBLE);
        if (male > 0) {
            tv_male_chip.setVisibility(View.VISIBLE);
            tv_male_chip.setText(male + " Male");
        }
        if (female > 0) {
            tv_female_chip.setVisibility(View.VISIBLE);
            tv_female_chip.setText(female + " Female");
        }
        if (kids > 0) {
            tv_kid_chip.setVisibility(View.VISIBLE);
            tv_kid_chip.setText(kids + " Kid");
        }
        tv_driver_name.setText(tripInfo.getDriver_name());
        Glide.with(this).load(tripInfo.getPhoto_uri()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("TAG", "Failed to load driver image " + e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(iv_rider);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
//        mGoogleMap.setMapStyle(mapStyleOptions);
        if (mCurrentLocation != null)
            addCustomMarker(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng startPoint = new LatLng(route.get(0).latitude, route.get(0).longitude);
        AddAPointMarker(startPoint);
//        AddStartPointMarker(startPoint);
//        mGoogleMap.addMarker(new MarkerOptions().position(startPoint));
        LatLng endPoint = new LatLng(route.get(route.size() - 1).latitude, route.get(route.size() - 1).longitude);
        AddBPointMarker(endPoint);
//        AddEndPointMarker(endPoint);
//        mGoogleMap.addMarker(new MarkerOptions().position(endPoint));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(startPoint, 14);
        mGoogleMap.moveCamera(location);
        DrawRoute();
    }

    private void addCustomMarker(LatLng latLng) {
//        Log.d("TAG", "addCustomMarker()");
        if (mGoogleMap == null) {
            return;
        }
        String path = getExternalFilesDir("profile").getPath();
        File file = new File(path + File.separator + "dp.png");
        // adding a marker on map with image from  drawable
        // adding a marker with image from URL using glide image loading library
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(file)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        String error = e.toString();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCurrLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, resource))));

                            }
                        });
                        return false;
                    }
                }).submit();
    }

    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    private void DrawRoute() {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(route);
        lineOptions.width(8);
        lineOptions.color(getResources().getColor(R.color.route_color));
        lineOptions.geodesic(true);
        RoutePolyline = mGoogleMap.addPolyline(lineOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_point_from:
                start = mGoogleMap.getCameraPosition().target;
                if (start_point) {
                    start_marker.remove();
                    start = null;
                    btn_starting_point.setImageResource(R.drawable.icon_add_point);
                    tv_start_point_location.setText("Add Start Point");
                    start_point = false;
                    trip_start_point=null;
                } else {
                    boolean checkStartPoint = PolyUtil.isLocationOnPath(start, route, true, 1000);
                    if (!checkStartPoint)
                        Toast.makeText(this, "Starting point must be on the rute", Toast.LENGTH_LONG).show();
                    else {
                        startIndex = PolyUtil.locationIndexOnPath(start, route, true, 1010);
//                        start_marker = mGoogleMap.addMarker(new MarkerOptions().position(start));
                        AddStartPointMarker(start);
                        assert start_marker != null;
                        try {
                            tv_start_point_location.setText(getAddress(start));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        start_point = true;
                        btn_starting_point.setImageResource(R.drawable.icon_remove_point);
                        trip_start_point = new GeoPoint(start.latitude, start.longitude);
                    }
                }
                break;
            case R.id.cl_point_to:
                end = mGoogleMap.getCameraPosition().target;
                if (end_point) {
                    end_marker.remove();
                    end = null;
                    tv_end_point_location.setText("Add Ending Point");
                    btn_ending_point.setImageResource(R.drawable.icon_add_point);
                    end_point = false;
                    trip_end_point=null;

                } else {
                    boolean checkEndPoint = PolyUtil.isLocationOnPath(end, route, true, 1000);
                    if (!checkEndPoint)
                        Toast.makeText(this, "Ending point must be on the rute", Toast.LENGTH_LONG).show();
                    else {
                        endIndex = PolyUtil.locationIndexOnPath(end, route, true, 1010);
                        AddEndPointMarker(end);
                        try {
                            tv_end_point_location.setText(getAddress(end));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        end_point = true;
                        btn_ending_point.setImageResource(R.drawable.icon_remove_point);
                        trip_end_point = new GeoPoint(end.latitude, end.longitude);
                    }
                }
                break;
            case R.id.iv_start_point:
                cl_point_from.performClick();
                break;
            case R.id.iv_end_point:
                cl_point_to.performClick();
                break;
            case R.id.iv_current_location:
                if (mCurrentGPSLocation != null) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentGPSLocation, 14));
                }
                break;
            case R.id.btn_plus_male:
                if (seats < available_seats) {
                    seats++;
                    male++;
                } else {
                    Toast.makeText(RideDetailActivity.this, "No more seat available", Toast.LENGTH_SHORT).show();
                }
                tv_male.setText(String.valueOf(male));
                break;
            case R.id.btn_minus_male:
                if (male > 0) {
                    seats--;
                    male--;
                }
                tv_male.setText(String.valueOf(male));
                break;
            case R.id.btn_plus_female:
                if (seats < available_seats) {
                    seats++;
                    female++;
                } else {
                    Toast.makeText(RideDetailActivity.this, "No more seat available", Toast.LENGTH_SHORT).show();
                }
                tv_female.setText(String.valueOf(female));
                break;
            case R.id.btn_minus_female:
                if (female > 0) {
                    seats--;
                    female--;
                }
                tv_female.setText(String.valueOf(female));
                break;
            case R.id.btn_plus_kids:
                if (seats < available_seats) {
                    seats++;
                    kids++;
                } else {
                    Toast.makeText(RideDetailActivity.this, "No more seat available", Toast.LENGTH_SHORT).show();
                }
                tv_kids.setText(String.valueOf(kids));
                break;
            case R.id.btn_minus_kids:
                if (kids > 0) {
                    seats--;
                    kids--;
                }
                tv_kids.setText(String.valueOf(kids));
                break;
            case R.id.btn_request_booking_ride_detail:
                SendBookingRequest();
                break;
            case R.id.iv_full_half_screen:
                ScreenControlClicked();
                break;
        }
    }

    private void ScreenControlClicked() {
        if (!full_screen) {
            cv_content.setVisibility(View.GONE);
            cl_map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            iv_screen_control.setImageResource(R.drawable.ic_close_full_screen);
            full_screen = true;
        } else {
            cl_map.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(com.intuit.sdp.R.dimen._320sdp)));
            cv_content.setVisibility(View.VISIBLE);
            iv_screen_control.setImageResource(R.drawable.ic_full_screen);
            full_screen = false;
        }
    }

    private void SendBookingRequest() {
        if (!prefs.getUserId().equals("")) {
            if (trip_start_point != null && trip_end_point != null) {
                if (seats != 0) {
                    ApplicationController.adPos = 0;
                    InterstitialAdSingleton.getInstance(this).showInterstitial(this);
                    //////////////
                    ArrayList<LatLng> pathSegment = new ArrayList<>();
                    for (int i = startIndex; i <= endIndex; i++) {
                        pathSegment.add(route.get(i));
                    }
                    double distance = SphericalUtil.computeLength(pathSegment) / 1000f;
                    Log.d("TAG","Distance is "+distance);
                    int estimatedCharges = (int) (distance * charges);
                    Log.d("TAG","Estimated charge is "+estimatedCharges);
                    int minCharge = estimatedCharges - 10;
                    if (minCharge < 0) {
                        minCharge = 1;
                    }
                    chargeRange = minCharge + "-" + (estimatedCharges + 10) + " Rs";
                    Log.d("TAG","Charge Range is "+estimatedCharges);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Confirm Your Booking")
                            .setMessage("Your estimated charges are " + chargeRange + " per person for one trip.\nDo you want to continue booking?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ShowInterstitialAd();
                                    ContinueBooking();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    alertDialog.show();
                    Log.d("TAG", "Selected Distance is " + distance);
                    ////////////


                } else {
                    Toast.makeText(RideDetailActivity.this, "Please select seats to proceed", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Please enter starting and ending point", Toast.LENGTH_SHORT).show();
            }

        } else {
            startActivity(new Intent(this, Signin.class));
        }

    }

    private void ContinueBooking() {
        String src_address = "";
        String dest_address = "";
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(trip_start_point.getLatitude(), trip_start_point.getLongitude(), 1);
            src_address = addresses.get(0).getAddressLine(0);
            addresses = geocoder.getFromLocation(trip_end_point.getLatitude(), trip_end_point.getLongitude(), 1);
            dest_address = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            Log.d("TAG", "Failed to convert latlng to address");
        }
        BookingRequestInfo bookingRequest = new BookingRequestInfo(
                tripInfo.getDriver_id()
                , prefs.getUserId()
                , prefs.getUserName()
                , prefs.getUserPhoneNo()
                , tripInfo.getTrip_id()
                , src_address
                , dest_address
                , trip_start_point
                , trip_end_point
                , seats
                , 0
                , male
                , female
                , kids
                , true
                , tripInfo.getDate()
                , chargeRange
        );
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).add(bookingRequest).addOnSuccessListener(documentReference -> {
            tv_btn_req_booking.setText("Request Sent");
//                            rl_btn_req_booking.setBackgroundColor(getResources().getColor(R.color.app_green));
            SendFCMMessage(fcm_token, prefs.getFcmServerKey());
            saveTripId(tripInfo.getTrip_id());
            startActivity(new Intent(RideDetailActivity.this, BookingStatus.class));
            finish();

        }).addOnFailureListener(e -> Toast.makeText(RideDetailActivity.this, "Cannot process request, Something went wrong", Toast.LENGTH_SHORT).show());
    }

    private void saveTripId(String trip_id) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        TripRequestDao tripRequestDao = carPoolDb.tripRequestDao();
                        TripRequestsData tripRequestsData = new TripRequestsData();
                        tripRequestsData.TripId = tripInfo.getTrip_id();
                        tripRequestDao.insertAll(tripRequestsData);
                    }
                }
        ).start();
    }

    private void GetDriverInfo(String driver_id) {
        mDBRef.collection(getString(R.string.USERS)).document(driver_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserInfo driver = documentSnapshot.toObject(UserInfo.class);
                assert driver != null;
                tv_phone_no.setText(driver.getPhone_no());
                tv_vehicle_no.setText(driver.getVehicle_no());
                fcm_token = driver.getFcm_token();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private static void SendFCMMessage(String fcm_token, String key) {//https://stackoverflow.com/questions/37435750/how-to-send-device-to-device-messages-using-firebase-cloud-messaging
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", "Booking Request");
                    dataJson.put("body", "New Booking request received");
                    dataJson.put("identifier", "NewBookingRequestReceived");
//                    dataJson.put("click_action", "OPEN_ACTIVITY");
                    json.put("data", dataJson);
                    json.put("to", "key=" + fcm_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + key)
//                            .header("Authorization", "key=AAAAfwizu9A:APA91bEvm_n25duhG-ioEXpkUqZuPneAw_SdpmBrHh6rhxcdasiW_dmLO7DzVpnk-5R-S8E62Iwy-jGUikIdLy6W3WnyjEhRiOMav_VRWPNUJZN7-DQ9vuU5vCHbXq99gVPKJ55BMN-q")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    Log.d("TAG", "Error sending fcm " + e.getMessage());
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();
    }

    private String getAddress(LatLng latlng) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        return addresses.get(0).getAddressLine(0);
    }

    private void AddStartPointMarker(LatLng start_point) {

        BitmapDescriptor StartMarkerIcon = BitmapDescriptorFactory.fromBitmap(startMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(start_point)
                .icon(StartMarkerIcon);
        start_marker = mGoogleMap.addMarker(markerOptions);
    }

    private void AddAPointMarker(LatLng start_point) {

        BitmapDescriptor StartMarkerIcon = BitmapDescriptorFactory.fromBitmap(AMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(start_point)
                .icon(StartMarkerIcon);
        mGoogleMap.addMarker(markerOptions);
    }

    private void AddBPointMarker(LatLng start_point) {

        BitmapDescriptor StartMarkerIcon = BitmapDescriptorFactory.fromBitmap(BMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(start_point)
                .icon(StartMarkerIcon);
        mGoogleMap.addMarker(markerOptions);
    }

    private void AddEndPointMarker(LatLng end_point) {

        BitmapDescriptor EndMarkerIcon = BitmapDescriptorFactory.fromBitmap(endMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(end_point)
                .icon(EndMarkerIcon);
        end_marker = mGoogleMap.addMarker(markerOptions);
    }

    private Bitmap getMarkerBitmapFromView2(View view, int identifier) {
        //identifier 1 for start point 2 for end point
        if (identifier == 1) {
            mMarkerImageViewRoute.setImageResource(R.drawable.ic_marker_start_point);
            mMarkerTextView.setText("Start Point");
        } else if (identifier == 2) {
            mMarkerImageViewRoute.setImageResource(R.drawable.ic_marker_end_point);
            mMarkerTextView.setText("End Point");
        }
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerBitmapForAB(View view, int identifier) {
        //identifier 1 for start point 2 for end point
        if (identifier == 1) {
            mABMarkerImageView.setImageResource(R.drawable.icon_start_point);
            mABMarkerTextView.setText("A");
        } else if (identifier == 2) {
            mABMarkerImageView.setImageResource(R.drawable.icon_end_point);
            mABMarkerTextView.setText("B");
        }
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RideDetailActivity.this, UserSearchActivity.class));
        finish();
    }

    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }

//    private String getCurrentDate() {
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//        String Date = df.format(c);
//        return Date;
//    }
}
