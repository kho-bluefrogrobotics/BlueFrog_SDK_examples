package com.bfr.welcomeproto;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;

import com.bfr.buddy.utils.values.FloatingWidgetVisibility;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.welcomeproto.grafcet.*;


public class MainActivity extends BuddyActivity {

    private final String TAG = "Grafcet Example";

    // UI elements
    private Button resetBtn; //to reset the grafcet
    private CheckBox startChckBx; //to start the grafcet sequence
    private Switch enableWheels;

    //grafcet definition
//    private MoveSequence moveSequenceGrafcet = new MoveSequence( "MoveSequence");
//    private YesSequence yesSequence = new YesSequence( "YesSequence");
    private MainGrafcet mainGrafcet = new MainGrafcet ("mainGrafcet");
    private LookingForSomeone lookinForSomeoneGrafcet = new LookingForSomeone( "LookingForSomeone");
    private InitGrafcet initGrafcet = new InitGrafcet( "InitGrafcet");
    private Interact interactGrafcet = new Interact( "InteractGrafcet");
    private ComeHere comeHereGrafcet = new ComeHere( "ComeHereGrafcet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // LInk to UI
        resetBtn = findViewById(R.id.resetBtn);
        startChckBx = findViewById(R.id.startChckBx);
        enableWheels = findViewById(R.id.enableSwitch);

        /**UI listeners*/
        // Reset button
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // reset grafcet
//                moveSequenceGrafcet.go = false;
//                moveSequenceGrafcet.step_num = 0;
//                yesSequence.go = false;
//                yesSequence.step_num = 0;

                mainGrafcet.go = false;
                mainGrafcet.step_num = 0;
                lookinForSomeoneGrafcet.go = false;
                lookinForSomeoneGrafcet.step_num = 0;
                interactGrafcet.go = false;
                interactGrafcet.step_num=0;
                comeHereGrafcet.go = false;
                comeHereGrafcet.step_num=0;
                ComeHere.followMe.stop();

                initGrafcet.go = false;
                initGrafcet.step_num = 0;
            }
        }); // end listener

        // start checkbox
        startChckBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set start signal for grafcet
//                moveSequenceGrafcet.go = isChecked;
//                yesSequence.go = isChecked;

                mainGrafcet.go = isChecked;


            } // end Onchecked
        }); // end listener

        // enable / disable the motor
        enableWheels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if checked
                if (isChecked)
                {
                    // Enable wheels
                    BuddySDK.USB.enableWheels(1, 1, new IUsbCommadRsp.Stub() {
                        @Override
                        public void onSuccess(String s) throws RemoteException {
                        }
                        @Override
                        public void onFailed(String s) throws RemoteException {
                        }
                    }); // end Enablewheels
                }
                else // else if unchecked
                {
                    // Disable wheels
                    BuddySDK.USB.enableWheels(0, 0, new IUsbCommadRsp.Stub() {
                        @Override
                        public void onSuccess(String s) throws RemoteException {
                        }

                        @Override
                        public void onFailed(String s) throws RemoteException {
                        }
                    }); // end Enablewheels

                }  // end if chedcked or not

            } // end Onchecked
        });// end listener

        /**
         * Grafcet
         */
//        moveSequenceGrafcet.start();
//        yesSequence.start();

        mainGrafcet.start();
        lookinForSomeoneGrafcet.start();
        initGrafcet.start();
        interactGrafcet.start();
        comeHereGrafcet.start();


    }  // End onCreate


    @Override
    public void onPause() {
    super.onPause();
    Log.i(TAG, "onPause");
    // stop grafcet
//    moveSequenceGrafcet.stop();
//    yesSequence.stop();

    mainGrafcet.stop();
    lookinForSomeoneGrafcet.stop();
    initGrafcet.stop();
    interactGrafcet.start();
    comeHereGrafcet.start();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        // stop grafcet
//        moveSequenceGrafcet.stop();
//        yesSequence.stop();
        MoveSequence.step_num=0;
        YesSequence.step_num=0;

        mainGrafcet.stop();
        lookinForSomeoneGrafcet.stop();
        mainGrafcet.step_num=0;
        lookinForSomeoneGrafcet.step_num=0;
        initGrafcet.stop();
        initGrafcet.step_num=0;
        interactGrafcet.stop();
        interactGrafcet.step_num=0;
        comeHereGrafcet.stop();
        comeHereGrafcet.step_num=0;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // restart grafcet
//        moveSequenceGrafcet.start();
//        yesSequence.start();

        mainGrafcet.start();
        lookinForSomeoneGrafcet.start();
        initGrafcet.start();
        interactGrafcet.start();
        comeHereGrafcet.start();
    }


    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {

        BuddySDK.UI.setCloseWidgetVisibility(FloatingWidgetVisibility.ON_TOUCH);
        BuddySDK.UI.setMenuWidgetVisibility(FloatingWidgetVisibility.ON_TOUCH);
    }


}