package com.sharerideexpense.easycarpool.DriverPanel.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sharerideexpense.easycarpool.Activities.ApplicationController;
import com.sharerideexpense.easycarpool.Ads.InterstitialAdSingleton;
import com.sharerideexpense.easycarpool.DriverPanel.Adaptors.AcceptedBookingRequestAdaptor;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Interface.onCancelAcceptedRequest;
import com.sharerideexpense.easycarpool.classes.BookingRequestInfo;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.sharerideexpense.easycarpool.classes.UserInfo;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AcceptedBookingRequests extends Fragment implements onCancelAcceptedRequest {
    private ArrayList<BookingRequestsMerged> bookingRequestsList = new ArrayList<>();
    private ArrayList<TripInfo> tripInfoList = new ArrayList<>();
    private RecyclerView rv_booking_request;
    private AcceptedBookingRequestAdaptor adaptor;
    private FirebaseFirestore mDBRef;
    private ArrayList<BookingRequestInfo> bookingRequestList = new ArrayList<>();
    private Skeleton skeleton;
    private Timer timer;
    private MySharedPreferences mSharedPreferences;
    private boolean DataReceived = false;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Activity activity;
    private SwipeRefreshLayout swipeRefresh;
    LinearLayout ll_empty;
    private boolean booking_req_get = false;
    private boolean trip_req_get = false;

    public AcceptedBookingRequests() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accepted_trip_requests, container, false);
        Init(view);
        RunQuery();
        return view;
    }

    private void RunQuery() {
        skeleton.showSkeleton();
        bookingRequestList.clear();
        tripInfoList.clear();
        bookingRequestsList.clear();
        CollectionReference BookingRequestRef = mDBRef.collection(getString(R.string.BOOKING_REQUEST));
        CollectionReference tripInfoRef = mDBRef.collection(getString(R.string.TRIPS));

        BookingRequestRef.whereEqualTo(getString(R.string.DRIVER_ID), mSharedPreferences.getUserId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                        String id = ds.getId();
                        BookingRequestInfo breq = ds.toObject(BookingRequestInfo.class);
                        assert breq != null;
                        if (breq.getStatus() == 1 && breq.isActive()) {
                            breq.setRequest_id(id);
                            bookingRequestList.add(breq);
//                                assert breq != null;
//                            tripInfoRef.document(breq.getTrip_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    if (documentSnapshot != null) {
//                                        TripInfo tripInfo = documentSnapshot.toObject(TripInfo.class);
//                                        tripInfoList.add(tripInfo);
//                                    }
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(requireActivity(), "Failed to get data, Something went wrong", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }
                    }
                })
                .addOnFailureListener(e ->
                        booking_req_get = false)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        booking_req_get = true;
//                        MergeResults();

                    }
                });
        tripInfoRef.whereEqualTo(getString(R.string.DRIVER_ID), mSharedPreferences.getUserId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                        TripInfo tripInfo = qds.toObject(TripInfo.class);
                        tripInfo.setTrip_id(qds.getId());
                        tripInfoList.add(tripInfo);
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Failed to get data, Something went wrong", Toast.LENGTH_SHORT).show();
                    trip_req_get = false;
                }).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                trip_req_get = true;
                            }
                        }
                );
        MergeResults();
    }


    private void Init(View view) {
        mSharedPreferences = new MySharedPreferences(requireContext());
        ll_empty = view.findViewById(R.id.ll_empty_list);
        mDBRef = FirebaseFirestore.getInstance();
        rv_booking_request = view.findViewById(R.id.rv_accepted_booking_requests);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RunQuery();
            }
        });
        rv_booking_request.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptor = new AcceptedBookingRequestAdaptor(requireContext(), bookingRequestsList, this);
        rv_booking_request.setAdapter(adaptor);
        skeleton = SkeletonLayoutUtils.applySkeleton(rv_booking_request, R.layout.item_accepted_booking_request);


    }


    private void MergeResults() {
//        while (!DataReceived){

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (trip_req_get && booking_req_get) {
                    for (int i = 0; i < bookingRequestList.size(); i++) {
                        TripInfo trip = new TripInfo();
                        BookingRequestsMerged bookingRequest = new BookingRequestsMerged();
                        for (int j = 0; j < tripInfoList.size(); j++) {
                            if (tripInfoList.get(j).getTrip_id().equals(bookingRequestList.get(i).getTrip_id())) {
                                trip = tripInfoList.get(j);
                            }
                        }
//                        trip = tripInfoList.get(i);
                        BookingRequestInfo b_req = bookingRequestList.get(i);
                        bookingRequest.setRequest_id(b_req.getRequest_id());
                        bookingRequest.setDate(trip.getDate());
                        bookingRequest.setTime(trip.getTime());
                        bookingRequest.setVehicle_name(trip.getVehicle_name());
                        bookingRequest.setVehicle_no(trip.getVehicle_no());
                        bookingRequest.setPick_location(b_req.getReq_pick_location());
                        bookingRequest.setDrop_location(b_req.getReq_drop_location());
                        bookingRequest.setRoute(trip.getRoute());
                        bookingRequest.setDriver_id(b_req.getDriver_id());
                        bookingRequest.setPassenger_id(b_req.getPassenger_id());
                        bookingRequest.setTrip_id(b_req.getTrip_id());
                        bookingRequest.setSeats_booked(b_req.getSeats_booked());
                        bookingRequest.setReq_start_lat(b_req.getStarting_point().getLatitude());
                        bookingRequest.setReq_start_lng(b_req.getStarting_point().getLongitude());
                        bookingRequest.setReq_end_lat(b_req.getEnding_point().getLatitude());
                        bookingRequest.setReq_end_lng(b_req.getEnding_point().getLongitude());
                        bookingRequest.setMale(b_req.getMale());
                        bookingRequest.setFemale(b_req.getFemale());
                        bookingRequest.setTrip_name(trip.getTrip_name());
                        bookingRequest.setKids(b_req.getKids());
                        bookingRequest.setPassenger_phone_no(b_req.getPassenger_phone_no());
                        bookingRequest.setPassenger_name(b_req.getPassenger_name());
                        bookingRequest.setCharges_range(b_req.getCharges_range());
                        bookingRequestsList.add(bookingRequest);
                    }
                    timer.cancel();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                            skeleton.showOriginal();
                            if (bookingRequestList.size() == 0) {
                                ll_empty.setVisibility(View.VISIBLE);
                            } else {
                                ll_empty.setVisibility(View.GONE);
                            }
                            adaptor.notifyDataSetChanged();
                        }
                    });
                    DataReceived = true;
                }
                // this code will be executed after 2 seconds
            }
        }, 500, 2000);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }


    @Override
    public void onCancelRequest(int male, int female, int kids, String request_id, String trip_id, String passenger_id, int position) {
//        ShowInterstitialAd();
        mDBRef.collection(getString(R.string.TRIPS)).document(trip_id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    TripInfo tripInfo = documentSnapshot.toObject(TripInfo.class);
                    int booked = male + female + kids;
                    Map<String, Object> map = new HashMap<>();
                    assert tripInfo != null;
                    if (tripInfo.getMale() - male >= 0) {
                        map.put(getString(R.string.MALE), tripInfo.getMale() - male);
                    } else {
                        map.put(getString(R.string.MALE), 0);
                    }
                    if (tripInfo.getFemale() - female >= 0) {
                        map.put(getString(R.string.FEMALE), tripInfo.getFemale() - female);
                    } else {
                        map.put(getString(R.string.FEMALE), 0);
                    }
                    if (tripInfo.getKids() - kids >= 0) {
                        map.put(getString(R.string.KIDS), tripInfo.getKids() - kids);
                    } else {
                        map.put(getString(R.string.KIDS), 0);
                    }
                    int totalSeatsRemaining = tripInfo.getRemaining_seats() + booked;
                    if (totalSeatsRemaining > tripInfo.getSeating_capacity()) {
                        map.put(getString(R.string.REMAINING_SEATS), tripInfo.getSeating_capacity());
                    } else {

                        map.put(getString(R.string.REMAINING_SEATS), totalSeatsRemaining);
                    }
                    mDBRef.collection(getString(R.string.TRIPS)).document(trip_id).update(map)
                            .addOnSuccessListener(unused -> {
                                mDBRef.collection(getString(R.string.BOOKING_REQUEST)).document(request_id).update(getString(R.string.ACTIVE), false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mDBRef.collection(getString(R.string.USERS)).document(passenger_id).get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    UserInfo user = documentSnapshot.toObject(UserInfo.class);
                                                    SendFCMMessage(user.getFcm_token(), mSharedPreferences.getFcmServerKey());
                                                    bookingRequestsList.remove(position);
                                                    adaptor.notifyDataSetChanged();
                                                }).addOnFailureListener(e -> {

                                                });
                                    }
                                }).addOnFailureListener(e -> {

                                });
                            }).addOnFailureListener(e -> {

                            });
                }).addOnFailureListener(e -> {

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
                    dataJson.put("body", "One of your ride booking has cancelled by the rider");
                    dataJson.put("identifier", "RiderCancelledRequest");
//                    dataJson.put("click_action", "OPEN_ACTIVITY");
                    json.put("data", dataJson);
                    json.put("to", "key=" + fcm_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + key)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                } catch (Exception e) {
                    Log.d("TAG", "Error sending fcm " + e.getMessage());
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();
    }

//    private void ShowInterstitialAd() {
//        ApplicationController.adPos++;
//        if (ApplicationController.adPos >= 2) {
//            ApplicationController.adPos = 0;
//            InterstitialAdSingleton.getInstance(activity).showInterstitial(activity);
//        }
//    }
}