package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.MotionDetection;
import com.bfr.buddy.vision.shared.enums.BuddyCamera;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;

public class motion extends Fragment {

    private Button mStartBtn, mStopBtn, mGetBtn, mSetThresBtn;
    private TextView resultText;
    private EditText thresText;
    private ImageView mPreviewCamera;
    private RadioGroup cameraRG;
    private RadioButton wideAngleRB, zoomRB;

    private BuddyCamera cameraId= BuddyCamera.WIDE_ANGLE;


    //Element to display frame from Camera
    private Handler mHandler = new Handler();
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame(cameraId));
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
        View view =  inflater.inflate(R.layout.fragment_motion, container, false);

        //Link to UI
        mStartBtn = view.findViewById(R.id.buttonStart);
        mStopBtn = view.findViewById(R.id.buttonStop);
        mGetBtn = view.findViewById(R.id.buttonGet);
        mSetThresBtn = view.findViewById(R.id.buttonSet);
        thresText = view.findViewById(R.id.textThres);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);
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

        /*** Motion detection*/
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BuddySDK.Vision.startMotionDetection(cameraId);
                    mHandler.post(mRunnablePreviewFrame);
                } catch (IllegalStateException e) {
                  e.printStackTrace();
                }
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.stopMotionDetection(cameraId);
                mHandler.removeCallbacksAndMessages(null);
            }
        });

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MotionDetection motionResult = BuddySDK.Vision.getMotionDetection(cameraId);

                resultText.setText("Mvt= " +   BuddySDK.Vision.motionDetect(cameraId) + " "+
                        motionResult.getAmplitude()
                        + " " + motionResult.getX()
                        + " " + motionResult.getY());
            }
        });

        mSetThresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.setMotionThres(cameraId, Float.parseFloat(thresText.getText().toString()));
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