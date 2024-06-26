package com.bfr.welcomeproto.grafcet;


import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.services.companion.Task;
import com.bfr.buddysdk.services.companion.TaskCallback;
import com.bfr.welcomeproto.utils.bfr_Grafcet;

public class ComeHere extends bfr_Grafcet {


    public ComeHere(String mname) {
        super(mname);
        this.grafcet_runnable = mysequence;

    }

    private ComeHere grafcet=this;

    // Static variable (to manage the grafcet from outside)
    public static int step_num =0;
    public static boolean go = false;
    final static int INTERVAL_MIN = 350;
    final static int INTERVAL_MAX = 450;
    private int mIntervalleHist = INTERVAL_MIN;
    private final int WIDTH_THRES = 30;
    private float speed = 10F;

    private int previous_step = 0;
    private double time_in_curr_step = 0;
    private boolean bypass = false;

    // Last position where a face was detected
    public int lastValidPosYes = 0;
    public int lastValidPosNo = 0;
    // Lost faces
    public static int noFacesTrials = 0;



    private long _TIMEOUT = 9000;
    // Calling out the user
    private long lastCallingTime = 0;
    private  final long  TIME_TO_CALL = 7000;
    private  final long  MAX_ATTEMPTS = 4;

    private int rnd_idx = 0;

   Detections faces;

    public static Task followMe;
    public static String followStatus = "";

    // called at end of mvt
    private IUsbCommadRsp iUsbCommadRsp = new IUsbCommadRsp.Stub(){
        @Override
        public void onSuccess(String success) throws RemoteException {
            Log.i("GRAFCET NO", "success --------------- : " + success);
        }
        @Override
        public void onFailed(String error) throws RemoteException {
            Log.i("GRAFCET NO", "error --------------- : " + error);
        }
    };

    // called at end of speech
    private ITTSCallback iTTSResp = new ITTSCallback.Stub() {
        @Override
        public void onSuccess(String s) throws RemoteException {

        }

        @Override
        public void onPause() throws RemoteException {

        }

        @Override
        public void onResume() throws RemoteException {

        }

        @Override
        public void onError(String s) throws RemoteException {

        }
    };
    
    // Define the sequence/grafcet to be executed
    // runable for grafcet
    private Runnable mysequence = new Runnable()
    {
        @Override
        public void run()
        {

            try
            {

            // if step changed
            if( !(step_num == previous_step)) {
                // display current step
                Log.i(name, "Current step : " + step_num );
                // update
                previous_step = step_num;

                // start counting time in current step
                time_in_curr_step = System.currentTimeMillis();
                //reset bypass
                bypass = false;
            }
            else // still in the same step
            {
                // if time > ?? seconds
                if ((System.currentTimeMillis()-time_in_curr_step > _TIMEOUT) && step_num >0)
                {
                    // activate bypass
                    bypass = true;
                }
            } // end if step = same


            // which grafcet step?
            switch (step_num) {
                case 0: // Wait for checkbox
                    //wait until check box
                    if (go) {
                        //reset
                        noFacesTrials = 0;
                        BuddySDK.UI.setFacialExpression(FacialExpression.NEUTRAL);
                        // go to next step
                        step_num = 1;
                        
                    }
                    break;


                case 1 : // call
                        BuddySDK.Speech.startSpeaking("Hey! Coucou !Tu veux parler un peu avec moi?" );
                        step_num = 2;

                case 2: // wait end of speach
                    if(BuddySDK.Speech.isReadyToSpeak())
                        step_num=5;
                    break;

                case 5: // wait for face
                    if(BuddySDK.Vision.detectFace().getNumOfDetections()>0)
                    {
                        Log.i(name, "FACE DETECTED" );
                        step_num = 10;
                    }
                    break;

                case 10: // start WatchMe
                    followMe = BuddySDK.Companion.createFollowMeTask();

                    followMe.start(new TaskCallback() {
                        @Override
                        public void onStarted() {
                            Log.i(name, "Starting FollowMe" );
                            followStatus ="started";
                        }

                        @Override
                        public void onSuccess(String s) {
                            followStatus ="finished";
                        }

                        @Override
                        public void onCancel() {
                            followStatus ="cancel";
                        }

                        @Override
                        public void onError(String s) {
                            followStatus ="error";
                        }
                    });

                    step_num = 15;
                    break;

                case 15: // wait end of followMe
                    step_num = 999;
                    break;

                case 999: // exit grafcet
                    go=false;
                    step_num=0;
                break;

                default :
                    // go to next step
                    step_num = 0;
                    break;
            } //End switch

            }  // end try
            catch (Exception e) {
                Log.e(name, Log.getStackTraceString(e));
            }

        } // end run

    }; // end new runnable


}
