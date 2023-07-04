package com.bfr.sdkv2vision;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddyCompatActivity;
import com.bfr.buddysdk.BuddySDK;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BuddyCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPager myViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        myViewPager = new MyViewPager(this);
        viewPager2.setAdapter(myViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Will be called once all BFR services are initialized.
    @Override
    public void onSDKReady() {

        // transfer the touch information to BuddyCore in the background
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));
        
    }

}