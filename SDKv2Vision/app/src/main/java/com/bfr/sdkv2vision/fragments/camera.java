package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class camera extends Fragment {

    private ImageView mPreviewCamera;
    private Handler mHandler = new Handler();
    CheckBox displayBox;
    //Element to display frame from Camera
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getGrandAngleFrame());
                mHandler.postDelayed(this, 30);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        Button myButton = (Button) view.findViewById(R.id.buttonStart);

        mPreviewCamera = view.findViewById(R.id.previewCam);
        displayBox = view.findViewById(R.id.displayChckbox);
        displayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mHandler.post(mRunnablePreviewFrame);
                else
                    mHandler.removeCallbacksAndMessages(null);
            }
        });

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        displayBox.setChecked(false);
    }
}