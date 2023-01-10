package com.sharerideexpense.easycarpool.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sharerideexpense.easycarpool.Interface.OnPlaceClick;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.classes.PlaceInfo;

import java.util.ArrayList;

public class PlacesAdaptor extends RecyclerView.Adapter<PlacesAdaptor.MyViewHolder> {
    ArrayList<PlaceInfo> mAddressList=new ArrayList<>();
    Context context;
    OnPlaceClick placeClick;

    public PlacesAdaptor(ArrayList<PlaceInfo> str, Context context, OnPlaceClick placeClick) {
        this.mAddressList=str;
        this.context = context;
        this.placeClick = placeClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_address.setText(mAddressList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeClick.placeClicked(mAddressList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_address = itemView.findViewById(R.id.tv_item_address);
        }
    }
}
