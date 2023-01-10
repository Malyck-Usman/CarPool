package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

public class RequestMapDetail extends AppCompatActivity implements OnMapReadyCallback {
    FrameLayout fl_map;
    GoogleMap mGoogleMap;
    Bitmap startMarkerIcon, endMarkerIcon,AMarkerIcon,BMarkerIcon;
    private View mMarkerView,mABMarkerView;
    private ImageView mMarkerImageView,mABMarkerImageView;
    private TextView mMarkerTextView,mABMarkerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_map_detail);
        Init();
    }

    private void Init() {
//        ShowInterstitialAd();
        mMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_route_markers, null);
        mMarkerImageView = (ImageView) mMarkerView.findViewById(R.id.iv_marker_image);
        mMarkerTextView = (TextView) mMarkerView.findViewById(R.id.tv_marker_name);
        startMarkerIcon = getMarkerBitmapFromView(mMarkerView, 1);
        endMarkerIcon = getMarkerBitmapFromView(mMarkerView, 2);
        mABMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_a_b_markers, null);
        mABMarkerTextView = (TextView) mABMarkerView.findViewById(R.id.tv_marker_name);
        mABMarkerImageView = (ImageView) mABMarkerView.findViewById(R.id.iv_marker_image);
        AMarkerIcon = getMarkerBitmapForAB(mABMarkerView, 1);
        BMarkerIcon = getMarkerBitmapForAB(mABMarkerView, 2);
        fl_map = findViewById(R.id.fl_map_container);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

    }

    private void getData(Intent intent) {
        BookingRequestsMerged bookingRequest = intent.getExtras().getParcelable(getString(R.string.BOOKING_REQUEST_DETAILS));
//        GeoPoint trip_start_point = new GeoPoint(bookingRequest.getReq_start_lat(),bookingRequest.getReq_start_lng());
//        GeoPoint trip_end_point = new GeoPoint(bookingRequest.getReq_end_lat(),bookingRequest.getReq_end_lng());
        ArrayList<LatLng> route = new ArrayList<LatLng>();
        route = (ArrayList<LatLng>) PolyUtil.decode(bookingRequest.getRoute());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(bookingRequest.getReq_start_lat(),bookingRequest.getReq_start_lng()), 12);
        mGoogleMap.moveCamera(cameraUpdate);
        LatLng user_start_point=new LatLng(bookingRequest.getReq_start_lat(),bookingRequest.getReq_start_lng());
        LatLng user_end_point=new LatLng(bookingRequest.getReq_end_lat(),bookingRequest.getReq_end_lng());
        LatLng start_point=new LatLng(route.get(0).latitude,route.get(0).longitude);
        LatLng end_point=new LatLng(route.get(route.size()-1).latitude,route.get(route.size()-1).longitude);
      AddAPointMarker(start_point);
      AddBPointMarker(end_point);
      AddStartPointMarker(user_start_point);
      AddEndPointMarker(user_end_point);
        DrawRoute(route);
    }

    private void DrawRoute(ArrayList<LatLng> route) {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(route);
        lineOptions.width(8);
        lineOptions.color(getResources().getColor(R.color.route_color));
        lineOptions.geodesic(true);
         mGoogleMap.addPolyline(lineOptions);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
//        mGoogleMap.setMapStyle(mapStyleOptions);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            getData(intent);
        }    }
    private Bitmap getMarkerBitmapFromView(View view, int identifier) {
        //identifier 1 for start point 2 for end point
        if (identifier == 1) {
            mMarkerImageView.setImageResource(R.drawable.ic_marker_start_point);
            mMarkerTextView.setText("Start Point");
        } else if (identifier == 2) {
            mMarkerImageView.setImageResource(R.drawable.ic_marker_end_point);
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
    private void AddEndPointMarker(LatLng end_point) {

        BitmapDescriptor EndMarkerIcon = BitmapDescriptorFactory.fromBitmap(endMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(end_point)
                .icon(EndMarkerIcon);
          mGoogleMap.addMarker(markerOptions);
    }
    private void AddStartPointMarker(LatLng start_point) {

        BitmapDescriptor StartMarkerIcon = BitmapDescriptorFactory.fromBitmap(startMarkerIcon);
        MarkerOptions markerOptions = new MarkerOptions().position(start_point)
                .icon(StartMarkerIcon);
        mGoogleMap.addMarker(markerOptions);
    }
//    private void ShowInterstitialAd() {
//        ApplicationController.adPos++;
//        if (ApplicationController.adPos >= 2) {
//            ApplicationController.adPos = 0;
//            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
//        }
//    }
}