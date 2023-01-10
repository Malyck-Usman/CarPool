package com.sharerideexpense.easycarpool.UserPanel.Adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Activities.RideDetailActivity;
import com.sharerideexpense.easycarpool.classes.TripInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RideSearchAdaptor extends RecyclerView.Adapter<RideSearchAdaptor.myViewHolder> {
    Context context;
    ArrayList<TripInfo> tripList = new ArrayList<>();

    public RideSearchAdaptor(Context context, ArrayList<TripInfo> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search_ride, parent, false);
        return new RideSearchAdaptor.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        TripInfo trip = tripList.get(position);
        holder.tv_vehicle_name.setText(trip.getVehicle_name());
        holder.tv_driver_name.setText(String.valueOf(trip.getDriver_name()));
        holder.tv_pick_up.setText(trip.getPick_location());
        holder.tv_drop_off.setText(trip.getDrop_location());
        holder.tv_departure_time.setText(trip.getTime());
        holder.tv_departure_date.setText(trip.getDate());
        holder.tv_trip_type.setText(trip.getTrip_type());
        holder.tv_charges.setText(trip.getCharges_per_km());
        if (!(trip.getDistance() == 0.0f)) {
            DecimalFormat df = new DecimalFormat("###.#");
            holder.tv_distance.setVisibility(View.VISIBLE);
            holder.tv_distance.setText(String.valueOf(df.format(trip.getDistance()) + " km\nAway"));
        }else {
            holder.tv_distance.setVisibility(View.GONE);
        }
        holder.tv_seats_available.setText(String.valueOf(trip.getRemaining_seats()));
        if(trip.getPhoto_uri()!=null){
        Glide.with(context).load(trip.getPhoto_uri()).into(holder.iv_driver);
        }
        holder.rl_min_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.rl_expended_view.getVisibility() == View.VISIBLE) {
                    holder.rl_expended_view.setVisibility(View.GONE);
                    holder.iv_min_max.setImageResource(R.drawable.icon_down);
                } else {

                    holder.rl_expended_view.setVisibility(View.VISIBLE);
                    holder.iv_min_max.setImageResource(R.drawable.icon_up);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RideDetailActivity.class);
                intent.putExtra(context.getResources().getString(R.string.TRIP_INFO_MERGED), trip);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_vehicle_name, tv_trip_type, tv_pick_up,
                tv_drop_off, tv_departure_time, tv_departure_date, tv_charges, tv_seats_available, tv_driver_name, tv_distance;
        FrameLayout cv_main;
        CircleImageView iv_driver;
        RelativeLayout rl_expended_view, rl_min_max;
        ImageView iv_min_max;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_vehicle_name = itemView.findViewById(R.id.tv_vehicle_name);
            tv_driver_name = itemView.findViewById(R.id.tv_driver_name);
            tv_pick_up = itemView.findViewById(R.id.tv_start_point_ride_detail);
            tv_drop_off = itemView.findViewById(R.id.tv_drop_off_ride_detail);
            tv_departure_time = itemView.findViewById(R.id.tv_departure_time_ride_detail);
            tv_departure_date = itemView.findViewById(R.id.tv_my_departure_date_ride_detail);
            tv_charges = itemView.findViewById(R.id.tv_charges_ride_details);
            tv_trip_type = itemView.findViewById(R.id.tv_trip_type);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_seats_available = itemView.findViewById(R.id.tv_seats_available_ride_detail);
            cv_main = itemView.findViewById(R.id.cv_search_ride);
            iv_driver = itemView.findViewById(R.id.iv_driver_image);
            rl_expended_view = itemView.findViewById(R.id.rl_expended_view);
            iv_min_max = itemView.findViewById(R.id.iv_min_max);
            rl_min_max = itemView.findViewById(R.id.rl_min_max);
            cv_main.setClipToOutline(true);
        }
    }

}
