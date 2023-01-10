package com.sharerideexpense.easycarpool.UserPanel.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Adaptors.BookingRequestDeclinedAdaptor;
import com.sharerideexpense.easycarpool.UserPanel.Classes.CarPoolDb;
import com.sharerideexpense.easycarpool.UserPanel.Classes.TripRequestsData;
import com.sharerideexpense.easycarpool.UserPanel.Interface.OnDeleteClicked;
import com.sharerideexpense.easycarpool.UserPanel.Interface.TripRequestDao;
import com.sharerideexpense.easycarpool.classes.BookingRequestInfo;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.sharerideexpense.easycarpool.classes.TripInfo;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class BookingRequestDeclined extends Fragment implements OnDeleteClicked {
    private RecyclerView mRecyclerView;
    private ArrayList<BookingRequestsMerged> mList = new ArrayList<>();
    private ArrayList<TripInfo> tripInfoList = new ArrayList<>();
    private ArrayList<BookingRequestInfo> bookingRequestList = new ArrayList<>();
    private BookingRequestDeclinedAdaptor mAdaptor;
    private FirebaseFirestore mDBRef;
    private Skeleton skeleton;
    private Timer timer;
    private Activity activity;
    Context context;
    private SwipeRefreshLayout swipeRefresh;
    CarPoolDb db;
    LinearLayout ll_empty;


    public BookingRequestDeclined() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_request_declined, container, false);
        Init(view);
        RunQuery();
        return view;
    }

    private void Init(View v) {
        db = Room.databaseBuilder(activity, CarPoolDb.class, getString(R.string.CARPOOL_DB)).build();
        ll_empty = v.findViewById(R.id.ll_empty_list);
        mRecyclerView = v.findViewById(R.id.rv_booking_requests_declined);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdaptor = new BookingRequestDeclinedAdaptor(mList, requireContext(), this);
        mRecyclerView.setAdapter(mAdaptor);
        skeleton = SkeletonLayoutUtils.applySkeleton(mRecyclerView, R.layout.item_booking_request_decline);
        mDBRef = FirebaseFirestore.getInstance();
        swipeRefresh = v.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RunQuery();
            }
        });
    }

    private void RunQuery() {
        skeleton.showSkeleton();
        bookingRequestList.clear();
        tripInfoList.clear();
        mList.clear();
        MySharedPreferences pref = new MySharedPreferences(requireContext());
        CollectionReference BookingRequestRef = mDBRef.collection(getString(R.string.BOOKING_REQUEST));
        CollectionReference tripInfoRef = mDBRef.collection(getString(R.string.TRIPS));

        BookingRequestRef.whereEqualTo(getString(R.string.PASSENGER_ID), pref.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot ds : task.getResult()) {
                        String id = ds.getId();
                        BookingRequestInfo bi = ds.toObject(BookingRequestInfo.class);
                        assert bi != null;
                        if (bi.getStatus() == -1) {
                            bi.setRequest_id(id);
                            bookingRequestList.add(bi);
                            assert bi != null;
                            tripInfoRef.document(bi.getTrip_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot != null) {
                                        TripInfo tripInfo = documentSnapshot.toObject(TripInfo.class);
                                        tripInfoList.add(tripInfo);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(requireActivity(), "Failed to get data, Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    MergeResults();
                }
            }
        });
    }

    private void MergeResults() {
//        while (!DataReceived){

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (bookingRequestList.size() == tripInfoList.size()) {
                    for (int i = 0; i < bookingRequestList.size(); i++) {
                        BookingRequestsMerged bookingRequest = new BookingRequestsMerged();
                        TripInfo trip = tripInfoList.get(i);
                        BookingRequestInfo bi = bookingRequestList.get(i);
                        bookingRequest.setRequest_id(bi.getRequest_id());
                        bookingRequest.setDate(trip.getDate());
                        bookingRequest.setTime(trip.getTime());
                        bookingRequest.setVehicle_name(trip.getVehicle_name());
                        bookingRequest.setPick_location(trip.getPick_location());
                        bookingRequest.setDrop_location(trip.getDrop_location());
                        bookingRequest.setRoute(trip.getRoute());
                        bookingRequest.setReq_start_lat(bi.getStarting_point().getLatitude());
                        bookingRequest.setReq_start_lng(bi.getStarting_point().getLongitude());
                        bookingRequest.setReq_end_lat(bi.getEnding_point().getLatitude());
                        bookingRequest.setReq_end_lng(bi.getEnding_point().getLongitude());
                        bookingRequest.setDriver_id(bi.getDriver_id());
                        bookingRequest.setPassenger_id(bi.getPassenger_id());
                        bookingRequest.setTrip_id(bi.getTrip_id());
                        bookingRequest.setSeats_booked(bi.getSeats_booked());
                        bookingRequest.setMale(bi.getMale());
                        bookingRequest.setFemale(bi.getFemale());
                        bookingRequest.setKids(bi.getKids());
                        bookingRequest.setVehicle_no(trip.getVehicle_no());
                        mList.add(bookingRequest);
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
                            mAdaptor.notifyDataSetChanged();
                        }
                    });
                }
                // this code will be executed after 2 seconds
            }
        }, 500, 2000);
    }


    @Override
    public void OnDeleteButtonClicked(int position, String request_id) {
        mDBRef.collection(getString(R.string.BOOKING_REQUEST)).document(request_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeTripId(mList.get(position).getTrip_id());
                mList.remove(position);
                mAdaptor.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
        Log.d("TAG", "BookingRequestDeclined OnPause Called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TAG", "BookingRequestDeclined OnStop Called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "BookingRequestDeclined OnDestroy Called");

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;

        this.context = context;
    }

    private void removeTripId(String trip_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TripRequestDao dao = db.tripRequestDao();
                TripRequestsData trd = dao.getTripObject(trip_id);
                if(trd!=null){
                    dao.delete(trd);
                }
            }
        }).start();
    }
}