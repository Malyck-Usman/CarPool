package com.sharerideexpense.easycarpool.DriverPanel.Adaptors;

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

public class OnBoardingAdaptor extends PagerAdapter {
    private Context context;
    private int[] slider_images = {
            R.drawable.ic_r_one,
            R.drawable.ic_r_two,
            R.drawable.ic_r_three,
    };
    private String[] slider_title = {
            "Complete Profile",
            "Create Trip",
            "Get Connected",
    };

    private String[] slider_desc = {
            "Create Your Driver Profile With Edit Profile",
            "Provide Complete Details Of Your Route And Charges",
            "Accept Rides From Passengers Commuting On Your Route",
    };

    @Override
    public int getCount() {
        return slider_title.length;
    }


    public OnBoardingAdaptor(Context context) {
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
        View view = layoutInflater.inflate(R.layout.on_boarding_item_container, container, false);

        ImageView img_banner = view.findViewById(R.id.img_banner);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_desc = view.findViewById(R.id.tv_desc);

        img_banner.setImageResource(slider_images[position]);
        tv_title.setText(slider_title[position]);
        tv_desc.setText(slider_desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
