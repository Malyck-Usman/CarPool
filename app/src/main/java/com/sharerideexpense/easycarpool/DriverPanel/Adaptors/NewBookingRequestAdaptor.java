package com.sharerideexpense.easycarpool.DriverPanel.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sharerideexpense.easycarpool.DriverPanel.Activities.RequestMapDetail;
import com.sharerideexpense.easycarpool.DriverPanel.Interface.OnBookingRequestInteract;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NewBookingRequestAdaptor extends RecyclerView.Adapter<NewBookingRequestAdaptor.MyViewHolder> {
    private Context context;
    private ArrayList<BookingRequestsMerged> bookingList = new ArrayList<>();
    private FirebaseFirestore mDBRef;
    private OnBookingRequestInteract bookingRequestInteract;

    public NewBookingRequestAdaptor(Context context, ArrayList<BookingRequestsMerged> bookingList, OnBookingRequestInteract bookingRequestInteract) {
        this.context = context;
        this.bookingList = bookingList;
        mDBRef = FirebaseFirestore.getInstance();
        this.bookingRequestInteract = bookingRequestInteract;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_booking_request, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = getPosition(holder);
        BookingRequestsMerged bookingRequest = bookingList.get(pos);
        holder.tv_SeatsAvailable.setText(String.valueOf(bookingRequest.getRemaining_seats()));
        holder.tv_startPoint.setText(bookingRequest.getPick_location());
        holder.tv_EndPoint.setText(bookingRequest.getDrop_location());
        holder.tv_trip_name.setText(bookingRequest.getTrip_name());
        holder.tv_charges_per_person.setText(bookingRequest.getCharges_range());
        holder.tv_phone_no.setText(bookingRequest.getPassenger_phone_no());
        holder.tv_vehicle_no.setText(bookingRequest.getVehicle_no());
        holder.tv_passenger_name.setText(bookingRequest.getPassenger_name());
        int male = bookingRequest.getMale();
        int female = bookingRequest.getFemale();
        int kids = bookingRequest.getKids();
        if (male > 0 || female > 0 || kids > 0)
            holder.rl_seats_booked.setVisibility(View.VISIBLE);
        if (male > 0) {
            holder.tv_male_chip.setVisibility(View.VISIBLE);
            holder.tv_male_chip.setText(male + " Male");
        }
        if (female > 0) {
            holder.tv_female_chip.setVisibility(View.VISIBLE);
            holder.tv_female_chip.setText(female + " Female");
        }
        if (kids > 0) {
            holder.tv_kid_chip.setVisibility(View.VISIBLE);
            holder.tv_kid_chip.setText(kids + " Kid");
        }
        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "position " + pos);
//                bookingList.remove(pos);
                bookingRequestInteract.OnDeclineClicked(pos);
            }
        });
        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bookingList.remove(pos);

                bookingRequestInteract.OnAcceptClicked(pos);

            }
        });
        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestMapDetail.class);
                intent.putExtra(context.getResources().getString(R.string.BOOKING_REQUEST_DETAILS), bookingRequest);
                context.startActivity(intent);
//                bookingRequestInteract.OnViewMapClicked(pos);
            }
        });


    }

    private int getPosition(MyViewHolder holder) {
        return holder.getAbsoluteAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_startPoint, tv_EndPoint, tv_SeatsAvailable, tv_trip_name, tv_male_chip, tv_female_chip, tv_kid_chip, tv_charges_per_person, tv_phone_no, tv_passenger_name, tv_vehicle_no;
        CardView btn_accept, btn_decline;
        ImageView viewOnMap;
        RelativeLayout rl_seats_booked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_startPoint = itemView.findViewById(R.id.tv_start_point);
            tv_EndPoint = itemView.findViewById(R.id.tv_drop_off);
//            tv_DepartureTime = itemView.findViewById(R.id.tv_departure_time_new_booking_request);
//            tv_DepartureDate = itemView.findViewById(R.id.tv_my_departure_date_new_booking_request);
            tv_SeatsAvailable = itemView.findViewById(R.id.tv_seats_available_new_booking_request);
            tv_trip_name = itemView.findViewById(R.id.tv_trip_name);
            btn_accept = itemView.findViewById(R.id.btn_accept_new_booking_request);
            btn_decline = itemView.findViewById(R.id.btn_decline_new_booking_request);
            viewOnMap = itemView.findViewById(R.id.btn_view_on_map);
            rl_seats_booked = itemView.findViewById(R.id.rl_seats_booked);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            tv_passenger_name = itemView.findViewById(R.id.tv_passenger_name);
            tv_charges_per_person = itemView.findViewById(R.id.tv_charges_per_person);
            tv_phone_no = itemView.findViewById(R.id.tv_phone_no);
            tv_vehicle_no = itemView.findViewById(R.id.tv_vehicle_no);


        }
    }
}
