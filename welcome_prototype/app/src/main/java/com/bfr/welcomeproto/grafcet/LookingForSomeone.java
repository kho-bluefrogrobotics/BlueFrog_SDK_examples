package com.bfr.welcomeproto.grafcet;

import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;

import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.Interpreter.Interpreter.BehaviourInterpreter;
import com.bfr.buddysdk.Interpreter.Interpreter.OnBehaviourAlgorithmListener;
import com.bfr.buddysdk.Interpreter.Interpreter.OnRunInstructionListener;
import com.bfr.buddysdk.Interpreter.Structures.Algorithm.BehaviourAlgorithmStorage;
import com.bfr.buddysdk.Interpreter.Structures.Instructions.Abstract.ABehaviourInstruction;

import com.bfr.buddysdk.services.companion.Task;
import com.bfr.buddysdk.services.companion.TaskCallback;
import com.bfr.welcomeproto.utils.bfr_Grafcet;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*************************************
Grafcet used to look for a user
********************** */
public class LookingForSomeone extends bfr_Grafcet  {

    private static final String TAG = "LOOKINGFORSOMEONE";


    /*** BI Reading*/

    private boolean biEnded = false;


    // random index to read BI
    int rnd_choice = 0;

//
//   // Static variable (to manage the grafcet from outside)
//
    public static boolean go = false;
    public static int step_num = 0;
    public static int previous_step = 0;
    private double time_in_curr_step = 0;
    private boolean bypass = false;
    private boolean endofspeak = false;

    //motor response
    private String MvtAck = "";

    // list of sentences to say
    List<String> _call_out= new ArrayList<>();

    public LookingForSomeone(String mname) {
        super(mname);
        this.grafcet_runnable = mysequence;




        // Verif (debug)
        for(int i =0; i<_call_out.size(); i++)
            Log.i("Readline ", "call out : " + _call_out.get(i));

    }


    private long lastBITime;
    private long lastCallOut;
    private  final long  TIME_TO_PLAY_BI = 11000;
    private  final long  TIME_TO_CALL_OUT = 5000;
    private long _TIMEOUT = 10000;
    final double THRESHOLD = 0.75;

    Detections humans;
    Task biTask=null;

    // At the end of movement
    private IUsbCommadRsp iUsbCommadRsp = new IUsbCommadRsp.Stub(){
        @Override
        public void onSuccess(String success) throws RemoteException {
//            Log.d(name, "success --------------- : " + success);
            MvtAck=success;
        }
        @Override
        public void onFailed(String error) throws RemoteException {
//            Log.d(name, "error --------------- : " + error);
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
                Log.d(name, "Current step : " + step_num );
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
                case 0: // Wait for start from Main Grafcet
                    if (go) {
                        // go to next step
                        lastCallOut = System.currentTimeMillis();
                        step_num = 1;
                        lastBITime= System.currentTimeMillis();
                    }
                    break;

                case 1: // Go right or left
                    Log.i(TAG, "No position: "+BuddySDK.Actuators.getNoPosition());
                    // if already on the left
                    if(BuddySDK.Actuators.getNoPosition()<=0)
                    { // go right
                        step_num = 10;
                    }
                    else
                    {// go left
                        step_num = 20;
                    }

                    // if time to play a BI
                    if (System.currentTimeMillis()-lastBITime > TIME_TO_PLAY_BI)
                    {
                        //comportamental BI
                        step_num = 40;
                    }
                    break;


                case 10: //Move to the Right
                    //motor response
                    MvtAck = "";
                    // start moving
                    BuddySDK.USB.buddySayNo(2.0f, 60.0f, iUsbCommadRsp);

                    // go to next step
                    step_num = 11;
                    break;

                case 11: //waiting for OK
                    if (MvtAck.toUpperCase().contains("OK")) {
                        // go to next step
                        step_num = 15;
                    }
                    break;

                case 15: // Wait for a person or Head completely on the right
//                    Log.i(TAG, "No position: "+BuddySDK.Actuators.getNoPosition());
                    try{
                        humans = BuddySDK.Vision.detectPerson();
                    } catch (Exception e) {
                        Log.e(TAG, "Error during exception: "+ Log.getStackTraceString(e));
                    }
                    //if person detected
                    if (humans.getNumOfDetections()>0)
                        step_num=30;
                    //else if arrived at the right position
//                    Log.i(TAG, "No positiobn: "+BuddySDK.Actuators.getNoPosition());
                    if(BuddySDK.Actuators.getNoPosition()>=50 || bypass)
                        step_num = 35;

                    break;

                case 20: //Move to the Left
                    MvtAck="";
                    // start moving
                    BuddySDK.USB.buddySayNo(2.0f, -60.0f, iUsbCommadRsp);

                    // go to next step
                    step_num = 21;
                    break;

                case 21: //waiting for OK
                    if (MvtAck.toUpperCase().contains("OK")) {
                        // go to next step
                        step_num = 25;
                    }
                    break;

                case 25: // Wait for a person or Head completely on the left
//                    Log.i(TAG, "No position: "+BuddySDK.Actuators.getNoPosition());
                    try{
                        humans = BuddySDK.Vision.detectPerson();
                    } catch (Exception e) {
                        Log.e("coucou", "Error during exception: "+ Log.getStackTraceString(e));
                    }
                    //if person detected
                    if (humans.getNumOfDetections()>0)
                        step_num=30;
                    //else if arrived at the left position
//                    Log.i(TAG, "No positiobn: "+BuddySDK.Actuators.getNoPosition());
                    if(BuddySDK.Actuators.getNoPosition()<=-50 || bypass)
                        step_num = 35;
                    break;


                case 30: // Person detected : Stop
                    Log.i(TAG, "HUMAN DETECTED ");
                    // Stop movement
                    BuddySDK.USB.buddyStopNoMove(iUsbCommadRsp);

                    // got to end of grafcet
                    step_num=999;
                    break;


                case 35: // Head at limit - Stop
                    // Stop movement
                    BuddySDK.USB.buddyStopNoMove(iUsbCommadRsp);

                    // if already on the left
                    if (BuddySDK.Actuators.getNoPosition() <= 0) { // go right
                        step_num = 10;
                    } else {// go left
                        step_num = 20;
                    }
                    break;


                case 40: // play a comportamental BI
                    biTask = BuddySDK.Companion.createBICategoryTask("idle");
                    biTask.start(new TaskCallback() {
                        @Override
                        public void onStarted() {

                        }

                        @Override
                        public void onSuccess(String s) {

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(String s) {

                        }
                    });

                    step_num = 41;
                    break;

                case 41 : // Waiting for ENd of BI
                    // update
                    MainGrafcet.lastBITime = System.currentTimeMillis();
                    step_num = 2000;
                    break;



                case 999: // exit grafcet
                    //reset
                    go =false;
                    step_num =0;
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
