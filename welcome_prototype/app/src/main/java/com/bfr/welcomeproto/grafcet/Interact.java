package com.bfr.welcomeproto.grafcet;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.speech.shared.ISTTCallback;
import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.speech.shared.STTResult;
import com.bfr.buddy.speech.shared.STTResultsData;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddy.vision.shared.FaceRecognition;
import com.bfr.buddy.vision.shared.IVisionRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.Interpreter.Interpreter.BehaviourInterpreter;
import com.bfr.buddysdk.Interpreter.Interpreter.OnBehaviourAlgorithmListener;
import com.bfr.buddysdk.Interpreter.Interpreter.OnRunInstructionListener;
import com.bfr.buddysdk.Interpreter.Structures.Algorithm.BehaviourAlgorithmStorage;
import com.bfr.buddysdk.Interpreter.Structures.Instructions.Abstract.ABehaviourInstruction;
import com.bfr.buddysdk.services.speech.STTTask;
import com.bfr.welcomeproto.R;
import com.bfr.welcomeproto.utils.bfr_Grafcet;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Interact extends bfr_Grafcet {


    // grafcet
    public static int step_num =0;
    private int previous_step = 0;
    private double time_in_curr_step = 0;
    private boolean timeout = false;
    private boolean hasChangedFace = false;
    private boolean bypass = false;
    private int TIMEOUT_CST = 7000;
    private boolean alreadyPlayed = false;

    // interface
    public static boolean go = false;


    //Requested speed
    private int yesSpeed = 30;
    //motor response
    private String YesMvtAck = "YesMove";

    Detections faces;
    FaceRecognition recognizedFace;
    String userName="";

    STTTask sttTask;
    STTResult sttResult;
    boolean endSTT= false;

    public Interact(String mname) {
        super(mname);
        this.grafcet_runnable = mysequence;

    }


    // At the end of movement
    private IUsbCommadRsp iUsbCommadRsp = new IUsbCommadRsp.Stub(){
        @Override
        public void onSuccess(String success) throws RemoteException {
            Log.i(name, "success --------------- : " + success);
        }
        @Override
        public void onFailed(String error) throws RemoteException {
            Log.i(name, "error --------------- : " + error);
        }
    };


    // Define the sequence/grafcet to be executed
   /* This provides a template for a grafcet.
   The sequence is as follows:
   - check the checkbox
   - Move the No from Left to right
   - Move the no from right to left
   - If the check box is unchecked then stop
   - if not, repeat
    */
    private Runnable mysequence = new Runnable() {
        @Override
        public void run()
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
                timeout = false;
            }
            else
            {
                // if time > 2s
                if ((System.currentTimeMillis()-time_in_curr_step > TIMEOUT_CST) && step_num >0)
                {
                    // activate bypass
                    bypass = true;
                    timeout = true;
                }
            }

            // which grafcet step?
            switch (step_num) {
                case 0: // Wait for checkbox


                    //wait until check box
                    if (go) {
                        // reset

                        // go to next step
                        step_num = 2;
                    }
                    break;


                case 2: // Say hello
                    BuddySDK.Speech.startSpeaking("Bonjour, comment ça va?");
                    step_num=5;
                    break;

                case 5: // wait end of speach
                    if(BuddySDK.Speech.isReadyToSpeak())
                        step_num=10;
                    break;

                case 10: // try Id face

                    faces = BuddySDK.Vision.detectFace();
                    recognizedFace = BuddySDK.Vision.recognizeFace(faces, 0);
                    step_num = 12;
                    break;

                case 12: // check recognition
                    if(recognizedFace.getScore()>=0.3)
                    {
                        BuddySDK.Speech.startSpeaking("Je te reconnais, tu t'appelle " + recognizedFace.getName() );

                    }
                        else
                    {
                    BuddySDK.Speech.startSpeaking("ça fait plaisir de voir de nouvelles têtes!" );
                        step_num = 50;
                    }

                    break;


                case 50: // wait end of speach
                    if(BuddySDK.Speech.isReadyToSpeak())
                        step_num=55;
                        break;

                case 55 : // ask name
                    BuddySDK.Speech.startSpeaking("On fait connaissance ? Comment est-ce que tu t'appelles?" );
                    step_num = 56;

                case 56: // wait end of speach
                    if(BuddySDK.Speech.isReadyToSpeak())
                        step_num=57;
                    break;

                case 57 : // ask name
                    sttTask = BuddySDK.Speech.createGoogleSTTTask(Locale.FRENCH);
                    //
                    endSTT = false;
                    sttTask.start(false, new ISTTCallback.Stub() {
                        @Override
                        public void onSuccess(STTResultsData sttResultsData) throws RemoteException {
                            userName = sttResultsData.getResults().get(0).getUtterance();
                            endSTT = true;
                        }

                        @Override
                        public void onError(String s) throws RemoteException {

                        }
                    });
                    step_num = 59;

                case 59: //wait end of listening
                    if(endSTT)
                        step_num = 60;
                    break;

                case 60 : // acknowlege user name
                    BuddySDK.Speech.startSpeaking("Eh bien \\\\pause=500\\\\ enchanté de te connaître  " + userName );
                    step_num = 65;
                    break;

                case 65 : // record face
                    faces = BuddySDK.Vision.detectFace();
                    BuddySDK.Vision.saveFace(faces, 0, userName, new IVisionRsp.Stub() {
                        @Override
                        public void onSuccess(String s) throws RemoteException {

                        }

                        @Override
                        public void onFailed(String s) throws RemoteException {

                        }
                    });
                    step_num = 65;
                    break;

                case 999: // exit grafcet
                    go = false;
                    step_num=0;
                    break;

                default:
                    //
                    step_num = 0;
            } //*end switch

        } // end of run

    }; // end of Runnable mysequence definition




}