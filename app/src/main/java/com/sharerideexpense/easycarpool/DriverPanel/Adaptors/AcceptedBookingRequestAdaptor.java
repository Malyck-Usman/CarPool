package com.sharerideexpense.easycarpool.DriverPanel.Adaptors;

import android.content.Context;
import android.content.Intent;
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
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Interface.onCancelAcceptedRequest;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AcceptedBookingRequestAdaptor extends RecyclerView.Adapter<AcceptedBookingRequestAdaptor.MyViewHolder> {
    Context context;
    ArrayList<BookingRequestsMerged> bookingList = new ArrayList<>();
    FirebaseFirestore mDBRef;
    onCancelAcceptedRequest onCancelAcceptedRequest;


    public AcceptedBookingRequestAdaptor(Context context, ArrayList<BookingRequestsMerged> bookingList, onCancelAcceptedRequest onCancelAcceptedRequest) {
        this.context = context;
        this.bookingList = bookingList;
        mDBRef = FirebaseFirestore.getInstance();
        this.onCancelAcceptedRequest = onCancelAcceptedRequest;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_accepted_booking_request, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = getPosition(holder);
        BookingRequestsMerged bookingRequest = bookingList.get(pos);
        holder.tv_SeatsBooked.setText(String.valueOf(bookingRequest.getSeats_booked()));
        holder.tv_startPoint.setText(bookingRequest.getPick_location());
        holder.tv_EndPoint.setText(bookingRequest.getDrop_location());
        holder.tv_passenger_name.setText(bookingRequest.getPassenger_name());
        holder.tv_trip_name.setText(bookingRequest.getTrip_name());
        holder.tv_charges_per_person.setText(bookingRequest.getCharges_range());
        if (!bookingRequest.getPassenger_phone_no().equals("")) {
            holder.tv_phone_no.setText(bookingRequest.getPassenger_phone_no());
        }
        if (!bookingRequest.getVehicle_no().equals("")) {
            holder.tv_vehicle_no.setText(bookingRequest.getVehicle_no());
        }
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

        holder.cv_cancel_booking_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelAcceptedRequest.onCancelRequest(male, female, kids, bookingRequest.getRequest_id(), bookingRequest.getTrip_id(), bookingRequest.getPassenger_id(), position);
            }
        });
        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestMapDetail.class);
                intent.putExtra(context.getResources().getString(R.string.BOOKING_REQUEST_DETAILS), bookingRequest);
                context.startActivity(intent);
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
        TextView tv_startPoint, tv_EndPoint, tv_male_chip, tv_female_chip, tv_kid_chip,
                tv_SeatsBooked, tv_trip_name, tv_passenger_name, tv_phone_no,tv_charges_per_person, tv_vehicle_no;
        //                tv_DepartureTime, tv_DepartureDate,
        RelativeLayout rl_seats_booked;
        ImageView viewOnMap;
        CardView cv_cancel_booking_request;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_startPoint = itemView.findViewById(R.id.tv_start_point);
            tv_EndPoint = itemView.findViewById(R.id.tv_drop_off);
            tv_trip_name = itemView.findViewById(R.id.tv_trip_name);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            tv_phone_no = itemView.findViewById(R.id.tv_phone_no);
            tv_vehicle_no = itemView.findViewById(R.id.tv_vehicle_no);
            rl_seats_booked = itemView.findViewById(R.id.rl_seats_booked);
            tv_SeatsBooked = itemView.findViewById(R.id.tv_seats_booked);
            tv_charges_per_person = itemView.findViewById(R.id.tv_charges_per_person);
            viewOnMap = itemView.findViewById(R.id.btn_view_on_map);
            cv_cancel_booking_request = itemView.findViewById(R.id.btn_cancel_booking_request);
            tv_passenger_name = itemView.findViewById(R.id.tv_passenger_name);


        }
    }

}
