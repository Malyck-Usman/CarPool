package com.sharerideexpense.easycarpool.DriverPanel.Adaptors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharerideexpense.easycarpool.Interface.DeleteTrip;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.TripInfo;

import java.util.ArrayList;

public class TripAdaptor extends RecyclerView.Adapter<TripAdaptor.myViewHolder> {
    private Context context;
    private ArrayList<TripInfo> tripList = new ArrayList<TripInfo>();
    private DeleteTrip deleteTrip;

    public TripAdaptor(Context context, ArrayList<TripInfo> tripList, DeleteTrip deleteTrip) {
        this.context = context;
        this.tripList = tripList;
        this.deleteTrip = deleteTrip;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_my_trips, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        TripInfo trip = tripList.get(position);
        holder.tv_trip_type.setText(trip.getTrip_type());
        holder.tv_trip_name.setText(trip.getTrip_name());
        holder.tv_seating_capacity.setText(String.valueOf(trip.getSeating_capacity()));
        holder.tv_pick_up.setText(trip.getPick_location());
        holder.tv_drop_off.setText(trip.getDrop_location());
        holder.tv_departure_time.setText(trip.getTime());
        if(!trip.getDate().equals("Date")){
            holder.tv_departure_date.setVisibility(View.VISIBLE);
        holder.tv_departure_date.setText(trip.getDate());}
        holder.tv_charges.setText(String.valueOf(trip.getCharges_per_km()));
        int male = trip.getMale();
        int female = trip.getFemale();
        int kids = trip.getKids();
        if (male > 0 || female > 0 || kids > 0)
            holder.rl_seats_booked.setVisibility(View.VISIBLE);
        else holder.rl_seats_booked.setVisibility(View.GONE);
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
        holder.btn_delete_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog=new AlertDialog.Builder(context).setTitle("Warning")
                                .setMessage("Do you really want to delete this trip ?" )
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteTrip.DeleteTripClick(trip.getTrip_id(),position);

                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setCancelable(true).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_trip_type, tv_trip_name, tv_seating_capacity, tv_pick_up, tv_drop_off, tv_departure_time, tv_departure_date, tv_charges, tv_male_chip, tv_female_chip, tv_kid_chip;
        ImageView btn_delete_trip;
        RelativeLayout rl_seats_booked;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_trip_type = itemView.findViewById(R.id.tv_my_trip_type);
            tv_trip_name = itemView.findViewById(R.id.tv_trip_name);
            tv_seating_capacity = itemView.findViewById(R.id.tv_seating_capacity_ride_detail);
            tv_pick_up = itemView.findViewById(R.id.tv_start_point);
            tv_drop_off = itemView.findViewById(R.id.tv_drop_off);
            tv_departure_time = itemView.findViewById(R.id.tv_my_departure_time);
            tv_departure_date = itemView.findViewById(R.id.tv_my_departure_date);
            tv_charges = itemView.findViewById(R.id.tv_my_charges);
            rl_seats_booked = itemView.findViewById(R.id.rl_seats_booked);
            tv_male_chip = itemView.findViewById(R.id.tv_male);
            tv_female_chip = itemView.findViewById(R.id.tv_female);
            tv_kid_chip = itemView.findViewById(R.id.tv_kid);
            btn_delete_trip = itemView.findViewById(R.id.btn_delete_trip);

        }
    }

}
