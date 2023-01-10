package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Adaptors.PlacesAdaptor;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.Interface.OnPlaceClick;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.DirectionPoints;
import com.sharerideexpense.easycarpool.classes.DirectionsJSONParser;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.PlaceInfo;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.errors.ApiException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChooseLocationActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher, View.OnClickListener, OnPlaceClick {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int GPS_REQUEST_CODE =6 ;
    GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    TextInputLayout edt_search_places;
    TextView tv_start_point, tv_end_point;
    Marker mCurrLocationMarker;
    CardView btn_done;
    ImageView btn_add_end_point, btn_add_start_point;
    CircleImageView btn_change_route, btn_current_location;
    PlacesAdaptor mPlacesAdaptor;
    Marker Startmarker, Endmarker;
    Polyline polyline;
    ArrayList<PlaceInfo> mAddressList;
    RecyclerView mRvPlaces;
    private LatLng start_point, end_point;
    ArrayList points = null;
    Skeleton skeleton;
    private boolean startPoint = false;
    private boolean endPoint = false;
    List<List<HashMap<String, String>>> polyLines;
    private int route_no = 0;
    private View mMarkerView;
    private ImageView mMarkerImageView;
    private TextView mMarkerTextView;
    Bitmap startMarkerIcon, endMarkerIcon;
    ConstraintLayout cl_from, cl_to;
    LatLng mCurrentLocation;
    MySharedPreferences prefs;
    String countryCode = "";
LinearLayout ll_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        Init();
    }

    private void Init() {
        prefs = new MySharedPreferences(this);
        mMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_route_markers, null);
        mMarkerImageView = (ImageView) mMarkerView.findViewById(R.id.iv_marker_image);
        mMarkerTextView = (TextView) mMarkerView.findViewById(R.id.tv_marker_name);
        startMarkerIcon = getMarkerBitmapFromView(mMarkerView, 1);
        endMarkerIcon = getMarkerBitmapFromView(mMarkerView, 2);
        setToolbar();
        btn_add_start_point = findViewById(R.id.iv_start_point);
        btn_add_end_point = findViewById(R.id.iv_end_point);
        btn_change_route = findViewById(R.id.iv_change_route);
        btn_change_route.setOnClickListener(this);
        tv_start_point = findViewById(R.id.start_point_location);
        tv_end_point = findViewById(R.id.end_point_location);
        btn_add_start_point.setOnClickListener(this);
        btn_add_end_point.setOnClickListener(this);
        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(this);
        cl_from = findViewById(R.id.cl_point_from);
        btn_current_location = findViewById(R.id.iv_current_location);
        btn_current_location.setOnClickListener(this);
        cl_to = findViewById(R.id.cl_point_to);
        cl_from.setOnClickListener(this);
        cl_to.setOnClickListener(this);
        ll_progress=findViewById(R.id.ll_progress);
        edt_search_places = findViewById(R.id.edt_search_places);
        edt_search_places.getEditText().addTextChangedListener(this);
//        edt_search_places.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    // hide virtual keyboard
//
//                    return true;
//                }
//                return false;
//            }
//        });
        edt_search_places.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search_places.getEditText().setText("");
                mRvPlaces.setVisibility(View.GONE);
            }
        });
        mAddressList = new ArrayList<>();
        mRvPlaces = findViewById(R.id.rv_locations);
        mRvPlaces.setLayoutManager(new LinearLayoutManager(this));
        mPlacesAdaptor = new PlacesAdaptor(mAddressList, ChooseLocationActivity.this, this);
        mRvPlaces.setAdapter(mPlacesAdaptor);
        skeleton = SkeletonLayoutUtils.applySkeleton(mRvPlaces, R.layout.item_address);
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.mv_select_location, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this); //calls on map ready
        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ImageView btn_toolbar = toolbar.findViewById(R.id.iv_toolbar);
        TextView tv_title = toolbar.findViewById(R.id.tv_toolbar);
        tv_title.setText("Choose Location");
        btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
//        mGoogleMap.setMapStyle(mapStyleOptions);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        getCurrentLocation();
//        mGoogleMap.setOnMarkerClickListener(this);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
//        } else {
//            requestLocationPermission();
//        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        mLocationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mLocationClient != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);

                Log.i("TAG", "Location: " + location.getLatitude() + " " + location.getLongitude());
                ////////
                countryCode = prefs.getCountryCode();
                if (countryCode.equals("")) {
                    try {
                        getCountryCode(new LatLng(location.getLatitude(), location.getLongitude()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ////////
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 12));
            }
        }
    };

//    private void requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                new AlertDialog.Builder(this)
//                        .setTitle("Location Permission Required")
//                        .setMessage("This app needs the Location permission, please accept to use location functionality")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(ChooseLocationActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();
//        }
//    }

//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            if(requestCode==MY_PERMISSIONS_REQUEST_LOCATION) {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//                    mGoogleMap.setMyLocationEnabled(true);
//
//                } else {
//                    requestLocationPermission();
//                }
//            }
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mRvPlaces.setVisibility(View.VISIBLE);
        skeleton.showSkeleton();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 3) {
            getPredictions(s);

        } else if (s.length() == 0) {
            mRvPlaces.setVisibility(View.GONE);

        }
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
                            skeleton.showOriginal();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        ShowInterstitialAd();
        switch (v.getId()) {
            case R.id.cl_point_from:
                if (!startPoint)
                    btn_add_start_point.performClick();
                break;
            case R.id.cl_point_to:
                if (!endPoint)
                    btn_add_end_point.performClick();
                break;
            case R.id.iv_start_point:
                try {
                    AddStartPoint();
                } catch (IOException e) {
                    ll_progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                break;
            case R.id.iv_end_point:
                try {
                    AddEndPoint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_done:
                if (startPoint && endPoint && points != null) {
                    DirectionPoints dp = new DirectionPoints();
                    dp.setLatLng(points);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(getString(R.string.ROUTE), dp);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Please select pick up and drop off point", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_change_route:
                ChangePolyRoute();
                break;
            case R.id.iv_current_location:
                if (mGoogleMap != null && mCurrentLocation != null)
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 12));
                break;
        }

    }

    private void drawRoute() {
        if (startPoint && endPoint) {
            if (polyline != null)
                polyline.remove();
            String url = null;
            try {
                url = getDirectionsUrl(start_point, end_point);
            } catch (IOException | InterruptedException | ApiException e) {
                e.printStackTrace();
            }
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        }
    }

    private void ChangePolyRoute() {
        if (start_point == null) {
            Toast.makeText(this, "Select pick up Point", Toast.LENGTH_SHORT).show();
        } else if (end_point == null) {
            Toast.makeText(this, "Select drop off point", Toast.LENGTH_SHORT).show();
        } else {
            mGoogleMap.clear();
            AddStartPointMarker();
            AddEndPointMarker();
            int routes = polyLines.size();
            if (route_no >= routes) {
                route_no = 0;
            }
            PolylineOptions lineOptions = null;
            points = new ArrayList();
            points.clear();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = polyLines.get(route_no);
            for (int j = 0; j < path.size(); j++) {
                HashMap point = path.get(j);
                double lat = Double.parseDouble((String) point.get("lat"));
                double lng = Double.parseDouble((String) point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(getResources().getColor(R.color.route_color));
            lineOptions.geodesic(true);
            polyline = mGoogleMap.addPolyline(lineOptions);
            route_no++;
        }
    }
    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }
    private void AddStartPointMarker() {

        BitmapDescriptor StartMarkerIcon = BitmapDescriptorFactory.fromBitmap(startMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(start_point)
                .icon(StartMarkerIcon);
        Startmarker = mGoogleMap.addMarker(markerOptions);
        ll_progress.setVisibility(View.GONE);
    }

    private void AddEndPointMarker() {

        BitmapDescriptor EndMarkerIcon = BitmapDescriptorFactory.fromBitmap(endMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(end_point)
                .icon(EndMarkerIcon);
        Endmarker = mGoogleMap.addMarker(markerOptions);
        ll_progress.setVisibility(View.GONE);
    }

    private Bitmap getMarkerBitmapFromView(View view, int identifier) {
        //identifier 1 for start point 2 for end point
        if (identifier == 1) {
            mMarkerImageView.setImageResource(R.drawable.ic_marker_start_point);
            mMarkerTextView.setText("Pick Up Point");
        } else if (identifier == 2) {
            mMarkerImageView.setImageResource(R.drawable.ic_marker_end_point);
            mMarkerTextView.setText("Drop Off Point");
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


    private void AddStartPoint() throws IOException {
        if (!startPoint) {
            ll_progress.setVisibility(View.VISIBLE);
            btn_add_start_point.setImageResource(R.drawable.icon_remove_point);
            LatLng center = mGoogleMap.getCameraPosition().target;
            start_point = center;
            tv_start_point.setText(getLocationAddress(start_point));
            startPoint = true;
            AddStartPointMarker();

        } else {
            btn_add_start_point.setImageResource(R.drawable.icon_add_point);
            Startmarker.remove();
            start_point = null;
            tv_start_point.setText("Adjust the marker on map and press add button");
            startPoint = false;
        }
        if (startPoint && endPoint) {
            ll_progress.setVisibility(View.VISIBLE);
            drawRoute();
        }
    }

    private String getLocationAddress(LatLng latlng) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        return addresses.get(0).getAddressLine(0);
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

    private void AddEndPoint() throws IOException {
        if (!endPoint) {
            ll_progress.setVisibility(View.VISIBLE);
            btn_add_end_point.setImageResource(R.drawable.icon_remove_point);
            LatLng center = mGoogleMap.getCameraPosition().target;
            end_point = center;
            tv_end_point.setText(getLocationAddress(end_point));
            endPoint = true;
            Objects.requireNonNull(edt_search_places.getEditText()).getText().clear();
            mRvPlaces.setVisibility(View.GONE);
            AddEndPointMarker();
        } else {
            btn_add_end_point.setImageResource(R.drawable.icon_add_point);
            Endmarker.remove();
            end_point = null;
            tv_end_point.setText("Adjust the marker on map and press add button");
            endPoint = false;
        }
        if (startPoint && endPoint) {
            ll_progress.setVisibility(View.VISIBLE);
            drawRoute();
        }
    }


//    @Override
//    public boolean onMarkerClick(@NonNull Marker marker) {
//        if (Objects.equals(marker.getTag(), count - 1)) {
//            marker.remove();
//            directionPointList.remove(count - 1);
//            count--;
//        }
//        return false;
//    }


    private class DownloadTask extends android.os.AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
            ll_progress.setVisibility(View.GONE);

        }
    }

    private class ParserTask extends android.os.AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            polyLines = result;
            ChangePolyRoute();

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng start, LatLng end) throws IOException, InterruptedException, ApiException {
        LatLng origin = start;
        LatLng dest = end;
        String parameters;
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Alternatives
        String alternatives = "alternatives=true";
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + prefs.getPlacesApiKey();
        //Way points
//          String  waypoints = "waypoints=via%3A33.703899%2C73.084241%7Cvia%3A33.725451%2C73.138169";
//        if (list.size() > 2) {
        StringBuilder waypoints = new StringBuilder("waypoints=");
        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + alternatives + "&" + key;
//        }

        // Building the parameters to the web service

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    @Override
    public void placeClicked(PlaceInfo placeInfo) {
        edt_search_places.getEditText().setText(placeInfo.getName());
        mRvPlaces.setVisibility(View.GONE);
        HideKeyboard();

        com.google.firebase.firestore.GeoPoint geoPoint;
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(placeInfo.getName(), 4);
            if (addresses.size() > 0) {
                String Name = addresses.get(0).getAddressLine(0);
                geoPoint = new com.google.firebase.firestore.GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                placeInfo.setName(Name);
                placeInfo.setGeoPoint(geoPoint);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), 15));
            }
        } catch (Exception e) {
            Log.d("TAG", "Failed to get lng lat " + e.getMessage());
        }
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_search_places.getEditText().getWindowToken(), 0);
    }

//    private class PlacesCall extends AsyncTask<String, Void, ArrayList<PlaceInfo>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            skeleton.showSkeleton();
//        }
//
//        @Override
//        protected ArrayList<PlaceInfo> doInBackground(String... s) {
//            ArrayList<PlaceInfo> mAddressList = new ArrayList<>();
//            OkHttpClient client = new OkHttpClient();
//            MediaType mediaType = MediaType.parse("text/plain");
//            RequestBody body = RequestBody.create(mediaType, "");
//            Request request = new Request.Builder()
//                    //places
//                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + s + "&components=country:pk&limit=4&types=geocode&language=en&key="+ ApplicationController.places_api_key)
//                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    Log.d("TAG", "FAiled to load data" + e.getMessage());
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    try {
//                        PlaceInfo placeInfo = new PlaceInfo();
//                        String responseData = response.body().string();
//                        JSONObject obj = new JSONObject(responseData);
//                        JSONArray places = obj.getJSONArray("predictions");
//                        for (int pos = 0; pos < places.length(); pos++) {
//                            JSONObject array1 = places.getJSONObject(pos);
//                            String placeName = array1.getString("description");
//                            placeInfo.setName(placeName);
////                            com.google.firebase.firestore.GeoPoint geoPoint;
////                            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
////                            List<Address> addresses = geoCoder.getFromLocationName(placeName, 4);
////                            if (addresses.size() > 0) {
////                                String Name = addresses.get(0).getAddressLine(0);
////                                geoPoint = new com.google.firebase.firestore.GeoPoint(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
////                                placeInfo.setName(Name);
////                                placeInfo.setGeoPoint(geoPoint);
//                            mAddressList.add(placeInfo);
////                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//            return mAddressList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<PlaceInfo> placeList) {
//            super.onPostExecute(placeList);
//
//            skeleton.showOriginal();
//            mPlacesAdaptor.notifyDataSetChanged();
//
//        }
//    }
}