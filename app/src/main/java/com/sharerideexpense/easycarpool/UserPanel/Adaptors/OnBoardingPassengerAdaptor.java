package com.sharerideexpense.easycarpool.UserPanel.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.sharerideexpense.easycarpool.R;

public class OnBoardingPassengerAdaptor extends PagerAdapter {
    private Context context;
    private int[] slider_images = {
            R.drawable.ic_p_one,
            R.drawable.ic_p_two,
            R.drawable.ic_p_three,
    };
    private String[] slider_title = {
            "Choose A Rider",
            "Select Your Route",
            "Send Ride Request",
    };
    private String[] lets_ride = {
            "Let's Ride",
            "",
            "",
    };
    private String[] ride_share = {
            "Rydea",
            "",
            "",
    };
    private String[] slogan = {
            "We Will Take You Anywhere",
            "",
            "",
    };
    private String[] slider_desc = {
            "Select From List Of Riders In Your Area",
            "Choose You Pick Up And Drop Off Location On Driver's Route.",
            "Send Your Ride Request And Travel Together At Fuel Sharing Basis",
    };
    @Override
    public int getCount() {
        return slider_title.length;
    }

    public OnBoardingPassengerAdaptor(Context context) {
        this.context = context;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.on_boarding_passenger_item_container, container, false);

        ImageView img_banner = view.findViewById(R.id.img_banner);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_desc = view.findViewById(R.id.tv_desc);
        TextView tv_lets_ride = view.findViewById(R.id.txt_lets_ride);
        TextView tv_ride_share = view.findViewById(R.id.txt_ride_share);
        TextView tv_slogan = view.findViewById(R.id.txt_slogan);

        img_banner.setImageResource(slider_images[position]);
        tv_title.setText(slider_title[position]);
        tv_desc.setText(slider_desc[position]);
        tv_lets_ride.setText(lets_ride[position]);
        tv_ride_share.setText(ride_share[position]);
        tv_slogan.setText(slogan[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
