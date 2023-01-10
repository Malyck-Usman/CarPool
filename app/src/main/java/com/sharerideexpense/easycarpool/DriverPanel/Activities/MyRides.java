package com.sharerideexpense.easycarpool.DriverPanel.Activities;

import static com.sharerideexpense.easycarpool.Activities.ApplicationController.googleSignInClient;
import static com.sharerideexpense.easycarpool.Activities.ApplicationController.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Activities.PrivacyPolicy;
import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.Ads.AppOpen;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.Ads.TemplateView;
import com.sharerideexpense.easycarpool.DriverPanel.Adaptors.TripAdaptor;
import com.sharerideexpense.easycarpool.Interface.DeleteTrip;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Activities.UserSearchActivity;
import com.sharerideexpense.easycarpool.classes.BookingRequestInfo;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRides extends AppCompatActivity implements View.OnClickListener, DeleteTrip, AppOpen {
    private String user_id;
    private RecyclerView rv_my_trips;
    private FirebaseFirestore mDBRef;
    private ArrayList<TripInfo> myTripList = new ArrayList<>();
    private TripAdaptor tripAdaptor;
    private View navigationDrawerLayout;
    LinearLayout ll_empty_list;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    MySharedPreferences preferences;
    private FloatingActionButton fab_add;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SwipeRefreshLayout swipeRefresh;
    FrameLayout fl_ad;
    View margin;
    ApplicationController mController;
    private TemplateView templateView;
    String FCM_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        Init();
        FetchData();
    }

    private void FetchData() {
        myTripList.clear();
        user_id = preferences.getUserId();
        mDBRef = FirebaseFirestore.getInstance();
        mDBRef.collection(getString(R.string.TRIPS)).whereEqualTo(getString(R.string.DRIVER_ID), user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                    TripInfo trip = qds.toObject(TripInfo.class);
                    trip.setTrip_id(qds.getId());
                    myTripList.add(trip);
                    Log.d("TAG", "On Success called ");
                }
                if (myTripList.size() > 0) {
                    ll_empty_list.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    tripAdaptor.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error RiderSearchActivity " + e.getMessage());
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void Init() {
        mController = ((ApplicationController) getApplicationContext());
        TemplateView templateView = findViewById(R.id.ad_container);
        mController.initNativeAd(templateView);
        mController.appOpenManager.setAppOpen(this);
        fl_ad = findViewById(R.id.fl_ad);
        margin = findViewById(R.id.ad_margin);

        preferences = new MySharedPreferences(this);
        FCM_KEY = preferences.getFcmServerKey();
        setTopBar();
        ll_empty_list = findViewById(R.id.ll_empty_list);
        rv_my_trips = findViewById(R.id.rv_rides);
        tripAdaptor = new TripAdaptor(this, myTripList, this);
        rv_my_trips.setAdapter(tripAdaptor);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
            }
        });
        rv_my_trips.setLayoutManager(new LinearLayoutManager(this));
        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(v -> {
            ShowInterstitialAd();
            if (preferences.getPlacesApiKey().equals("") || preferences.getFcmServerKey().equals("")) {
                getKeys();
            }
            if (preferences.getProfileUpdated()) {
                startActivity(new Intent(this, CreateTrip.class));
            } else {
                ShowAlertDialog();
            }
        });
        ShowUserData();
    }

    private void setTopBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        TextView tv_title = findViewById(R.id.tv_toolbar_main);
        tv_title.setText("My Rides");
//        NavigationView navigationView = findViewById(R.id.nav_view_driver);
        drawerLayout = findViewById(R.id.drawer_layout_driver);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationDrawerLayout = findViewById(R.id.drawer_menu);
//        navigationView.setNavigationItemSelectedListener(this);
//        headerView = navigationView.getHeaderView(0);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void ShowUserData() {
        MySharedPreferences prefs = new MySharedPreferences(this);
        TextView userName = (TextView) navigationDrawerLayout.findViewById(R.id.tv_drawer_user_name);
        TextView userEmail = (TextView) navigationDrawerLayout.findViewById(R.id.tv_drawer_email);
        CircleImageView iv_user = (CircleImageView) navigationDrawerLayout.findViewById(R.id.iv_drawer_user);
        userName.setText(preferences.getUserName());
        userEmail.setText(preferences.getUserEmail());
        if (!prefs.getUserImageUri().equals(""))
            iv_user.setImageURI(Uri.parse(preferences.getUserImageUri()));

        TextView tv_home = navigationDrawerLayout.findViewById(R.id.tv_drawer_home);
        TextView tv_create_trip = navigationDrawerLayout.findViewById(R.id.tv_drawer_create_trip);
        TextView tv_trip_request = navigationDrawerLayout.findViewById(R.id.tv_drawer_trip_requests);
        TextView tv_logout = navigationDrawerLayout.findViewById(R.id.tv_drawer_logout);
        TextView tv_edit_profile = navigationDrawerLayout.findViewById(R.id.tv_drawer_edit_profile);
        TextView tv_privacy_policy = navigationDrawerLayout.findViewById(R.id.tv_drawer_privacy_policy);
        CardView cv_passenger_mode = navigationDrawerLayout.findViewById(R.id.cv_drawer_passenger_mode);
        tv_home.setOnClickListener(this);
        tv_edit_profile.setOnClickListener(this);
        tv_create_trip.setOnClickListener(this);
        tv_trip_request.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);
        cv_passenger_mode.setOnClickListener(this);
    }


    private void ShowAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyRides.this);
        dialog.setTitle("Complete your profile");
        dialog.setMessage("Please complete your profile to create trip");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MyRides.this, RiderProfile.class));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    @Override
    public void onClick(View v) {
        ShowInterstitialAd();
        switch (v.getId()) {
            case R.id.tv_drawer_home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.tv_drawer_create_trip:
                if (preferences.getProfileUpdated()) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(this, CreateTrip.class));
                } else {
                    ShowAlertDialog();
                }
                break;
            case R.id.tv_drawer_trip_requests:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MyRides.this, RideRequests.class));
                break;
            case R.id.tv_drawer_privacy_policy:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MyRides.this, PrivacyPolicy.class));
                break;
            case R.id.tv_drawer_edit_profile:
                startActivity(new Intent(this, RiderProfile.class));
                break;
            case R.id.tv_drawer_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                mAuth.signOut();
                if (googleSignInClient != null)
                    googleSignInClient.signOut();
                preferences.clearData();
                startActivity(new Intent(this, Signin.class));
                finish();
                break;
            case R.id.cv_drawer_passenger_mode:
                drawerLayout.closeDrawer(GravityCompat.START);
                preferences.setUserMode(getString(R.string.PASSENGER_MODE));
                startActivity(new Intent(MyRides.this, UserSearchActivity.class));
                break;
        }
    }
    private void ShowInterstitialAd() {
        ApplicationController.adPos++;
        if (ApplicationController.adPos >= 3) {
            ApplicationController.adPos = 0;
            InterstitialAdSingleton.getInstance(this).showInterstitial(this);
        }
    }
    @Override
    public void DeleteTripClick(String trip_id, int position) {
        ArrayList<String> UserIdList = new ArrayList<>();
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).whereEqualTo(getString(R.string.TRIP_ID), trip_id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            BookingRequestInfo bookingRequest = qds.toObject(BookingRequestInfo.class);
                            bookingRequest.setRequest_id(qds.getId());
                            if (bookingRequest.getStatus() == 1) {
                                mDBRef.collection(getString(R.string.BOOKING_REQUEST)).document(bookingRequest.getRequest_id())
                                        .update(getString(R.string.ACTIVE), false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("TAG", "Active status changed successfully ");
                                            }
                                        });
                                UserIdList.add(bookingRequest.getPassenger_id());
                            }
                        }
                        if (UserIdList.size() > 0) {
                            for (int i = 0; i < UserIdList.size(); i++) {
                                mDBRef.collection(getString(R.string.USERS)).document(UserIdList.get(i)).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                                                SendFCMMessage(userInfo.getFcm_token(), FCM_KEY);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "Failed to get Fcm Ids " + e.getMessage());
                                            }
                                        });
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed to get User Ids " + e.getMessage());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mDBRef.collection(getString(R.string.TRIPS)).document(trip_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                myTripList.remove(position);
                                tripAdaptor.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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
                    dataJson.put("title", "Ride Cancelled");
                    dataJson.put("body", "One Of The Ride You Have Booked Has Been Canceled By The Rider");
                    dataJson.put("identifier", "RideCancelled");
                    json.put("data", dataJson);
                    json.put("to", "key=" + fcm_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + key)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    //Log.d(TAG,e+"");
                    Log.d("TAG", "Failed to deliver message " + e.getMessage());
                }
                return null;
            }
        }.execute();
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

    public void getKeys() {
        DatabaseReference mDBRef = FirebaseDatabase.getInstance().getReference();

        mDBRef.child(getString(R.string.API_KEYS)).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Log.d("TAG", "data snapshot " + dataSnapshot);
                        preferences.setPlacesApiKey((String) dataSnapshot.child(getString(R.string.PLACES_API_KEY)).getValue());
                        preferences.setFcmSeverKey((String) dataSnapshot.child(getString(R.string.FCM_KEY)).getValue());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed to load api keys " + e.getMessage());
                    }
                });
    }
}


