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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.Tracking;
import com.bfr.buddy.vision.shared.enums.BuddyCamera;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class tracking extends Fragment {

    private Button mStartBtn, mStopBtn, mGetBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;
    private CheckBox displayBox; private RadioGroup cameraRG;
    private RadioButton wideAngleRB, zoomRB;

    private BuddyCamera cameraId= BuddyCamera.WIDE_ANGLE;

    //Element to display frame from Camera
    private Handler mHandler = new Handler();
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                mHandler.postDelayed(this, 40);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tracking, container, false);

        //Link to UI
        mStartBtn = view.findViewById(R.id.buttonStart);
        mStopBtn = view.findViewById(R.id.buttonStop);
        mGetBtn = view.findViewById(R.id.buttonGet);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);
        displayBox = view.findViewById(R.id.displayChckbox);
        cameraRG = view.findViewById(R.id.cameraRadioGroup);
        wideAngleRB = view.findViewById(R.id.wideAngleRB);
        zoomRB = view.findViewById(R.id.zoomRB);


        // camera selection
        cameraRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(wideAngleRB.isChecked())
                    cameraId = BuddyCamera.WIDE_ANGLE;
                else
                    cameraId = BuddyCamera.ZOOM;

            }
        });

        /*** Tracking */
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BuddySDK.Vision.startTracking(cameraId);

                    mHandler.post(mRunnablePreviewFrame);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.stopTracking(cameraId);
                mHandler.removeCallbacksAndMessages(null);
            }
        });

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tracking targetResult = BuddySDK.Vision.getTracking(cameraId);

                resultText.setText("Target= " +   targetResult.isTrackingSuccessfull() + " "
                        + " " + targetResult.getLeftPos()
                        + " " + targetResult.getTopPos()  );
            }
        });

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
        //reset
        mHandler.removeCallbacksAndMessages(null);
        BuddySDK.Vision.stopMotionDetection(cameraId);
    }
}