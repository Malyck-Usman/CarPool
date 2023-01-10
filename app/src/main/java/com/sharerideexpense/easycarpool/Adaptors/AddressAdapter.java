package com.sharerideexpense.easycarpool.Adaptors;

import android.app.Activity;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.sharerideexpense.easycarpool.R;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private static final String TAG ="addressAdapter" ;
    private Activity context;
    private ArrayList<Address> addressList=new ArrayList<Address>() ;
    OnPlaceSelect onPlaceSelect;


    public AddressAdapter(Activity context, ArrayList<Address> address,OnPlaceSelect onPlaceSelect){
        this.context = context;
       addressList.addAll(address);
       this.onPlaceSelect=onPlaceSelect;

        Log.d("TAG", "getView: "+addressList.size());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listViewItem = LayoutInflater.from(context).inflate(R.layout.item_rv_address, null, true);

        return new MyViewHolder(listViewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

//        locatonTag = listViewItem.findViewById(R.id.imagebtn);
//        locatonTag.setFocusable(false);
//        locatonTag.setFocusableInTouchMode(false);
//        locatonTag.setClickable(true);
//        TextView locality = (TextView) holder.findViewById(R.id.locality);
//        TextView subadmin = (TextView) listViewItem.findViewById(R.id.subAdmin);

        Address address = addressList.get(position);
        Log.d(TAG, "address: " + address);
        String str = address.getExtras().getString("display_name","");
        int length = str.length();
        Log.d("TAG", "Extras "+length);
        String[] strings = str.split("\\,");
        String firstName =  strings[0];
        String subName = "";
        String sub_name = "";
        if (strings.length>1){
            for (int i =1; i<strings.length; i++){
                if (subName!="") subName = subName+", "+strings[i];
                else subName = strings[i];
                sub_name =  subName.replace("Azad Kashmir", "Pakistan");
            }
        }
        Log.d(TAG, "firstName: "+firstName);
        Log.d(TAG, "SecondName: "+subName);
        holder.tv_locality.setText(firstName);
        holder.tv_subItem.setText(sub_name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlaceSelect.onPlaceSelectClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_locality,tv_subItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_locality=itemView.findViewById(R.id.tv_locality);
            tv_subItem=itemView.findViewById(R.id.tv_subAdmin);

        }
    }
    public interface OnPlaceSelect{
         void onPlaceSelectClick(int position);
    }
}



