package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.FaceRecognition;
import com.bfr.buddy.vision.shared.IVisionRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;

public class facerecog extends Fragment {

    String TAG = "Face recognition";
    private Button mSaveBtn, mRecogBtn, mLoadBtn, mGetAllBtn, mGetTopkBtn, mDeleteBtn;
    private TextView resultText;
    private EditText nameText;
    private EditText indexText;
    private ImageView mPreviewCamera;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_facerecog, container, false);

        //Link to UI
        mSaveBtn = view.findViewById(R.id.buttonSave);
        mRecogBtn = view.findViewById(R.id.buttonRecog);
        mLoadBtn = view.findViewById(R.id.buttonLoad);
        mGetAllBtn = view.findViewById(R.id.buttonGetall);
        mGetTopkBtn = view.findViewById(R.id.buttonGetTopk);
        mDeleteBtn = view.findViewById(R.id.buttonDelete);
        resultText = view.findViewById(R.id.resultText);
        nameText = view.findViewById(R.id.editTextName);
        indexText = view.findViewById(R.id.editTextIndex);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        /*** Face recognition*/
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuddySDK.Vision.saveFace(BuddySDK.Vision.detectFace(),
                        0,
                        nameText.getText().toString(),
                        new IVisionRsp.Stub() {
                            @Override
                            public void onSuccess(String s) throws RemoteException {
                                Log.i(TAG, "Face saved successfully");
                            }

                            @Override
                            public void onFailed(String s) throws RemoteException {
                                Log.e(TAG, "Error saving face: " + s);
                            }
                        });

                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
            }
        });

        mRecogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultText.setText("Face recognized:\n"+
                BuddySDK.Vision.recognizeFace(BuddySDK.Vision.detectFace(), 0));
            }
        });

        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuddySDK.Vision.loadFaces(new IVisionRsp.Stub() {
                    @Override
                    public void onSuccess(String s) throws RemoteException {
                        Log.i(TAG, "Faces loaded successfully");
                    }

                    @Override
                    public void onFailed(String s) throws RemoteException {
                        Log.e(TAG, "Faces loading failed: " + s);
                    }
                });
            }
        });

        mGetAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] listOfNames = BuddySDK.Vision.getSavedNames();

                String toDisplay = "";
                for (int i=0; i<listOfNames.length; i++){
                    toDisplay = toDisplay + listOfNames[i] + "\n";
                }
                resultText.setText(toDisplay);
            }
        });

        mGetTopkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaceRecognition[] candidates = BuddySDK.Vision.getTopKResults(3);

                String toDisplay = "";
                for (int i=0; i<candidates.length; i++){
                    toDisplay = toDisplay + candidates[i].getName() + "\n";
                }
                resultText.setText(toDisplay);
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuddySDK.Vision.removeFace(Integer.parseInt(indexText.getText().toString()),
                        new IVisionRsp.Stub() {
                            @Override
                            public void onSuccess(String s) throws RemoteException {
                                Log.i(TAG, "Removed saved face successfully");
                            }

                            @Override
                            public void onFailed(String s) throws RemoteException {
                                Log.e(TAG, "Removing saved face loading failed: " + s);
                            }
                        });
            }
        });

        return view;
    }
}