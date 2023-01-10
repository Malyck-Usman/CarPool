package com.sharerideexpense.easycarpool.DriverPanel.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sharerideexpense.easycarpool.DriverPanel.Activities.RequestMapDetail;
import com.sharerideexpense.easycarpool.DriverPanel.Adaptors.NewBookingRequestAdaptor;
import com.sharerideexpense.easycarpool.DriverPanel.Interface.OnBookingRequestInteract;
import com.sharerideexpense.easycarpool.R;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class NewBookingRequests extends Fragment implements OnBookingRequestInteract {
    private ArrayList<BookingRequestsMerged> bookingRequestsList = new ArrayList<>();
    private ArrayList<TripInfo> tripInfoList = new ArrayList<>();
    private ArrayList<BookingRequestInfo> bookingRequestList = new ArrayList<>();
    private RecyclerView rv_booking_request;
    private NewBookingRequestAdaptor adaptor;
    private FirebaseFirestore mDBRef;
    private Skeleton skeleton;
    private Timer timer;
    private MySharedPreferences mSharedPreferences;
    private boolean DataReceived = false;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Activity activity;
    private Context context;
    BookingRequestsMerged bookingRequest;
    private SwipeRefreshLayout swipeRefresh;
    LinearLayout ll_empty;
    private boolean booking_req_get = false;
    private boolean trip_req_get = false;

    public NewBookingRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_trip_requests, container, false);
        Init(view);
//        GetData();
        RunQuery();
//        GetTripInfo getTripInfo = new GetTripInfo();
//        getTripInfo.execute();
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
                        BookingRequestInfo bi = ds.toObject(BookingRequestInfo.class);
                        assert bi != null;
                        if (bi.getStatus() == 0) {
                            bi.setRequest_id(id);
                            bookingRequestList.add(bi);
                        }
                    }
                }).addOnFailureListener(e -> {
                    booking_req_get = false;
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        booking_req_get = true;
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
                    trip_req_get = false;
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trip_req_get = true;
                    }
                });
        MergeResults();
    }

    private void Init(View view) {
        mSharedPreferences = new MySharedPreferences(requireContext());
        ll_empty = view.findViewById(R.id.ll_empty_list);
        mDBRef = FirebaseFirestore.getInstance();
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RunQuery();
            }
        });
        rv_booking_request = view.findViewById(R.id.rv_new_booking_requests);
        rv_booking_request.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptor = new NewBookingRequestAdaptor(requireContext(), bookingRequestsList, this);
        rv_booking_request.setAdapter(adaptor);
        skeleton = SkeletonLayoutUtils.applySkeleton(rv_booking_request, R.layout.item_new_booking_request);


    }


    private void MergeResults() {
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
                        BookingRequestInfo b_req = bookingRequestList.get(i);
                        bookingRequest.setRequest_id(b_req.getRequest_id());
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
                        bookingRequest.setRemaining_seats(trip.getRemaining_seats());
                        bookingRequest.setMale(b_req.getMale());
                        bookingRequest.setFemale(b_req.getFemale());
                        bookingRequest.setKids(b_req.getKids());
                        bookingRequest.setPassenger_phone_no(b_req.getPassenger_phone_no());
                        bookingRequest.setCharges_range(b_req.getCharges_range());
                        bookingRequest.setTrip_name(trip.getTrip_name());
                        bookingRequest.setPassenger_name(b_req.getPassenger_name());
                        bookingRequest.setVehicle_no(trip.getVehicle_no());
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
    public void OnAcceptClicked(int position) {
//        ShowInterstitialAd();
        bookingRequest = bookingRequestsList.get(position);
        int available_seats = bookingRequest.getRemaining_seats();
        int seats_booking_request = bookingRequest.getMale() + bookingRequest.getFemale() + bookingRequest.getKids();
        if (seats_booking_request > available_seats) {
            Toast.makeText(requireContext(), "Not enough seats available", Toast.LENGTH_SHORT).show();
        } else {
//            bookingRequestsList.get(position).setRemaining_seats(available_seats-seats_booking_request);
            ChangeStatus(position, 1);
        }
    }

    @Override
    public void OnDeclineClicked(int position) {
//        ShowInterstitialAd();
        ChangeStatus(position, -1);
    }

    @Override
    public void OnViewMapClicked(int position) {
        BookingRequestsMerged request = bookingRequestsList.get(position);
        Intent intent = new Intent(context, RequestMapDetail.class);
        intent.putExtra(context.getResources().getString(R.string.BOOKING_REQUEST_DETAILS), request);
        context.startActivity(intent);
    }

    private void ChangeStatus(int position, int value) {
        String fcm_token = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", value);

        mDBRef.collection(getString(R.string.BOOKING_REQUEST))
                .document(bookingRequestsList
                        .get(position)
                        .getRequest_id())
                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Status Updated id " + bookingRequestsList.get(position).getRequest_id());

                        mDBRef.collection(getString(R.string.USERS)).document(bookingRequestsList.get(position).getPassenger_id()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        UserInfo user = documentSnapshot.toObject(UserInfo.class);
                                        assert user != null;
                                        if (value == -1) {
                                            SendNotification(user.getFcm_token(), "Booking Request Declined", "Your Booking request has been Declined by the Rider", "BookingRequestDeclined", mSharedPreferences.getFcmServerKey());
                                        } else if (value == 1) {
                                            SendNotification(user.getFcm_token(), "Booking Request Accepted", "Your Booking request has been Accepted by the Rider", "BookingRequestAccepted", mSharedPreferences.getFcmServerKey());
                                        }
                                        bookingRequestsList.remove(position);
                                        adaptor.notifyDataSetChanged();
                                    }
                                });
                        if (value == 1) {
                            int available_seats = bookingRequest.getRemaining_seats();
                            int seats_booking_request = bookingRequest.getMale() + bookingRequest.getFemale() + bookingRequest.getKids();
                            int remaining_seats = available_seats - seats_booking_request;
                            int male = tripInfoList.get(position).getMale() + bookingRequest.getMale();
                            int female = tripInfoList.get(position).getFemale() + bookingRequest.getFemale();
                            int kids = tripInfoList.get(position).getKids() + bookingRequest.getKids();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put(getString(R.string.REMAINING_SEATS), remaining_seats);
                            map.put(getString(R.string.MALE), male);
                            map.put(getString(R.string.FEMALE), female);
                            map.put(getString(R.string.KIDS), kids);

                            mDBRef.collection(getString(R.string.TRIPS)).document(bookingRequestsList.get(position).getTrip_id()).update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG", "Remaining seats Updated ");
                                            RunQuery();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG", "Status Update Failed " + e.getMessage());
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Status Update Failed " + e.getMessage());
                    }
                });
    }


    private static void SendNotification(String fcm_token, String title, String body, String identifier, String key) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", body);
                    dataJson.put("title", title);
                    dataJson.put("identifier", identifier);
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
                    Log.d("TAG", "Error " + e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
        this.context = context;
    }

//    private void ShowInterstitialAd() {
//        ApplicationController.adPos++;
//        if (ApplicationController.adPos >= 2) {
//            ApplicationController.adPos = 0;
//            InterstitialAdSingleton.getInstance(context).showInterstitial(activity);
//        }
//    }
}