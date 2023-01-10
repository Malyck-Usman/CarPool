package com.sharerideexpense.easycarpool.UserPanel.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Interface.OnDeleteClicked;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingRequestDeclinedAdaptor extends RecyclerView.Adapter<BookingRequestDeclinedAdaptor.myViewHolder> {
    ArrayList<BookingRequestsMerged> mList=new ArrayList<>();
    Context context;
    OnDeleteClicked deleteClicked;

    public BookingRequestDeclinedAdaptor(ArrayList<BookingRequestsMerged> mList, Context context, OnDeleteClicked deleteClicked) {
        this.mList = mList;
        this.context = context;
        this.deleteClicked=deleteClicked;
    }

    @NonNull
    @Override
    public BookingRequestDeclinedAdaptor.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_booking_request_decline,null,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingRequestDeclinedAdaptor.myViewHolder holder, int position) {
        BookingRequestsMerged bookingRequest = mList.get(position);
        holder.tv_startPoint.setText(bookingRequest.getPick_location());
        holder.tv_EndPoint.setText(bookingRequest.getDrop_location());
        if (!bookingRequest.getDate().equals("Date")) {
            holder.tv_DepartureDate.setVisibility(View.VISIBLE);
            holder.tv_DepartureDate.setText(bookingRequest.getDate());
        }        holder.tv_DepartureTime.setText(bookingRequest.getTime());
        holder.tv_vehicle_name.setText(bookingRequest.getVehicle_name());
        holder.tv_vehicle_no.setText(bookingRequest.getVehicle_no());
        holder.tv_SeatsBooked.setText(String.valueOf(bookingRequest.getSeats_booked()));
        int male=bookingRequest.getMale();
        int female=bookingRequest.getFemale();
        int kids=bookingRequest.getKids();
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
                deleteClicked.OnDeleteButtonClicked(getAdaptorPosition(holder),bookingRequest.getRequest_id());
            }
        });

    }

    private int getAdaptorPosition(myViewHolder holder) {
      return   holder.getAbsoluteAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_startPoint, tv_EndPoint, tv_DepartureTime, tv_DepartureDate, tv_SeatsBooked, tv_vehicle_name,tv_driver_name,
                tv_male_chip,tv_female_chip,tv_kid_chip,tv_vehicle_no;
        CircleImageView iv_driver;
        ImageView iv_route,btn_delete;
        RelativeLayout rl_seats_booked;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_startPoint = itemView.findViewById(R.id.tv_start_point);
            tv_EndPoint = itemView.findViewById(R.id.tv_drop_off);
            tv_DepartureTime = itemView.findViewById(R.id.tv_my_departure_time);
            tv_DepartureDate = itemView.findViewById(R.id.tv_my_departure_date);
            tv_SeatsBooked = itemView.findViewById(R.id.tv_seats_booking_request_sent);
            tv_vehicle_name = itemView.findViewById(R.id.tv_vehicle_name);
            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            btn_delete = itemView.findViewById(R.id.btn_delete_trip);
            iv_route = itemView.findViewById(R.id.iv_route);
            iv_driver=itemView.findViewById(R.id.iv_driver_image);
            rl_seats_booked=itemView.findViewById(R.id.rl_seats_booked);
            tv_vehicle_no=itemView.findViewById(R.id.tv_vehicle_no);
        }
    }
}
