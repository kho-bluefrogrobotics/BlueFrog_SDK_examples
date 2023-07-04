package com.bfr.sdkv2vision;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bfr.sdkv2vision.fragments.aruco;
import com.bfr.sdkv2vision.fragments.detection;
import com.bfr.sdkv2vision.fragments.movement;

public class MyViewPager extends FragmentStateAdapter {
    public MyViewPager(@NonNull MainActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new aruco();
            case 1:
                return new detection();
            case 2:
                return new movement();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
