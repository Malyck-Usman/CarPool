package com.sharerideexpense.easycarpool.UserPanel.Adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.room.Room;

import com.sharerideexpense.easycarpool.DriverPanel.Activities.RequestMapDetail;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Classes.CarPoolDb;
import com.sharerideexpense.easycarpool.UserPanel.Classes.TripRequestsData;
import com.sharerideexpense.easycarpool.UserPanel.Interface.onCancelAcceptedRequest;
import com.sharerideexpense.easycarpool.classes.BookingRequestsMerged;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingRequestApprovedAdaptor extends RecyclerView.Adapter<BookingRequestApprovedAdaptor.myViewHolder> {
    ArrayList<BookingRequestsMerged> mList = new ArrayList<>();
    Context context;
    onCancelAcceptedRequest onCancelAcceptedRequest;
    CarPoolDb db;

    public BookingRequestApprovedAdaptor(ArrayList<BookingRequestsMerged> mList, Context context, onCancelAcceptedRequest onCancelAcceptedRequest) {
        this.mList = mList;
        this.context = context;
        this.onCancelAcceptedRequest = onCancelAcceptedRequest;
        db = Room.databaseBuilder(context, CarPoolDb.class, context.getString(R.string.CARPOOL_DB)).allowMainThreadQueries().build();
    }

    @NonNull
    @Override
    public BookingRequestApprovedAdaptor.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_request_approved, null, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingRequestApprovedAdaptor.myViewHolder holder, int position) {
        BookingRequestsMerged bookingRequest = mList.get(position);
        holder.tv_startPoint.setText(bookingRequest.getPick_location());
        holder.tv_EndPoint.setText(bookingRequest.getDrop_location());
        if (!bookingRequest.getDate().equals("Date")) {
            holder.tv_DepartureDate.setVisibility(View.VISIBLE);
            holder.tv_DepartureDate.setText(bookingRequest.getDate());
        }
        holder.tv_vehicle_no.setText(bookingRequest.getVehicle_no());
        holder.tv_DepartureTime.setText(bookingRequest.getTime());
        holder.tv_charges_per_person.setText(bookingRequest.getCharges_range());
        holder.tv_vehicle_name.setText(bookingRequest.getVehicle_name());
        holder.tv_rider_no.setText(bookingRequest.getDriver_phone_no());
        holder.tv_SeatsBooked.setText(String.valueOf(bookingRequest.getSeats_booked()));
        if (!bookingRequest.isActive()) {
            holder.rl_active.setVisibility(View.VISIBLE);
            TripRequestsData td = db.tripRequestDao().getTripObject(bookingRequest.getTrip_id());
            if (td != null)
                db.tripRequestDao().delete(td);
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
        holder.iv_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestMapDetail.class);
                intent.putExtra(context.getResources().getString(R.string.BOOKING_REQUEST_DETAILS), bookingRequest);
                context.startActivity(intent);
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to cancel booking ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onCancelAcceptedRequest.onCancelRequest(male, female, kids, bookingRequest.getRequest_id(), bookingRequest.getTrip_id(), bookingRequest.getDriver_id(), position);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
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
                tv_male_chip, tv_female_chip, tv_kid_chip,tv_charges_per_person, tv_rider_no, tv_vehicle_no;
        CircleImageView iv_driver;
        ImageView iv_route;
        RelativeLayout rl_seats_booked, rl_active;
        CardView btn_delete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_startPoint = itemView.findViewById(R.id.tv_start_point);
            tv_EndPoint = itemView.findViewById(R.id.tv_drop_off);
            tv_DepartureTime = itemView.findViewById(R.id.tv_my_departure_time);
            tv_DepartureDate = itemView.findViewById(R.id.tv_my_departure_date);
            tv_SeatsBooked = itemView.findViewById(R.id.tv_seats_booking_request_approved);
            tv_vehicle_name = itemView.findViewById(R.id.tv_vehicle_name);
            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_charges_per_person = itemView.findViewById(R.id.tv_charges_per_person);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            iv_route = itemView.findViewById(R.id.iv_route);
            iv_driver = itemView.findViewById(R.id.iv_driver_image);
            btn_delete = itemView.findViewById(R.id.btn_delete_booking_request);
            rl_seats_booked = itemView.findViewById(R.id.rl_seats_booked);
            rl_active = itemView.findViewById(R.id.rl_active);
            tv_rider_no = itemView.findViewById(R.id.tv_rider_no);
            tv_vehicle_no = itemView.findViewById(R.id.tv_vehicle_no);
        }
    }
}
