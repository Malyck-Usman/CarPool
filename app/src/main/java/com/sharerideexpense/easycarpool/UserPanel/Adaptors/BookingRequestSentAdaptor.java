package com.sharerideexpense.easycarpool.UserPanel.Adaptors;

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
import com.sharerideexpense.easycarpool.UserPanel.Interface.OnDeleteClicked;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingRequestSentAdaptor extends RecyclerView.Adapter<BookingRequestSentAdaptor.myViewHolder> {
    ArrayList<BookingRequestsMerged> mList = new ArrayList<>();
    Context context;
    OnDeleteClicked deleteClicked;

    public BookingRequestSentAdaptor(ArrayList<BookingRequestsMerged> mList, Context context, OnDeleteClicked deleteClicked) {
        this.mList = mList;
        this.context = context;
        this.deleteClicked = deleteClicked;
    }

    @NonNull
    @Override
    public BookingRequestSentAdaptor.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_request_sent, null, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingRequestSentAdaptor.myViewHolder holder, int position) {
        BookingRequestsMerged bookingRequest = mList.get(position);
        holder.tv_startPoint.setText(bookingRequest.getPick_location());
        holder.tv_EndPoint.setText(bookingRequest.getDrop_location());
        if (!bookingRequest.getDate().equals("Date")) {
            holder.tv_DepartureDate.setVisibility(View.VISIBLE);
            holder.tv_DepartureDate.setText(bookingRequest.getDate());
        }
        holder.tv_charges_per_person.setText(bookingRequest.getCharges_range());
        holder.tv_DepartureTime.setText(bookingRequest.getTime());
        holder.tv_vehicle_name.setText(bookingRequest.getVehicle_name());
        holder.tv_SeatsBooked.setText(String.valueOf(bookingRequest.getSeats_booked()));
        holder.tv_driver_phone_no.setText(bookingRequest.getDriver_phone_no());
        holder.tv_vehicle_no.setText(bookingRequest.getVehicle_no());
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
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bookingList.remove(pos);
                deleteClicked.OnDeleteButtonClicked(getAdaptorPosition(holder), bookingRequest.getRequest_id());
            }
        });
        holder.iv_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestMapDetail.class);
                intent.putExtra(context.getResources().getString(R.string.BOOKING_REQUEST_DETAILS), bookingRequest);
                context.startActivity(intent);
            }
        });

    }

    private int getAdaptorPosition(myViewHolder holder) {
        return holder.getAbsoluteAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_startPoint, tv_EndPoint, tv_DepartureTime, tv_DepartureDate, tv_SeatsBooked, tv_vehicle_name, tv_driver_name,
                tv_male_chip, tv_female_chip, tv_kid_chip, tv_charges_per_person, tv_driver_phone_no, tv_vehicle_no;
        CardView btn_delete;
        CircleImageView iv_driver;
        ImageView iv_route;
        RelativeLayout rl_seats_booked;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_startPoint = itemView.findViewById(R.id.tv_start_point);
            tv_EndPoint = itemView.findViewById(R.id.tv_drop_off);
            tv_DepartureTime = itemView.findViewById(R.id.tv_my_departure_time);
            tv_DepartureDate = itemView.findViewById(R.id.tv_my_departure_date);
            tv_charges_per_person = itemView.findViewById(R.id.tv_charges_per_person);
            tv_SeatsBooked = itemView.findViewById(R.id.tv_seats_booking_request_sent);
            tv_vehicle_name = itemView.findViewById(R.id.tv_vehicle_name);
            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            btn_delete = itemView.findViewById(R.id.btn_delete_booking_request_sent);
            iv_route = itemView.findViewById(R.id.iv_route);
            iv_driver = itemView.findViewById(R.id.iv_driver_image);
            rl_seats_booked = itemView.findViewById(R.id.rl_seats_booked);
            tv_driver_phone_no = itemView.findViewById(R.id.tv_rider_no);
            tv_vehicle_no = itemView.findViewById(R.id.tv_vehicle_no);

        }
    }
}
