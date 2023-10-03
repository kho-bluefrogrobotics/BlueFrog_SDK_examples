package com.bfr.welcomeproto.grafcet;


import android.util.Log;

import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.welcomeproto.utils.bfr_Grafcet;

public class MainGrafcet extends bfr_Grafcet  {


    public MainGrafcet(String mname) {
        super(mname);
        this.grafcet_runnable = mysequence;
    }

    // Static variable (to manage the grafcet from outside)
//    public static int step_num =0;
    public static boolean go = false;


//    private int previous_step = 0;
    private double time_in_curr_step = 0;
    private boolean bypass = false;
    public static int step_num = 0;

    // Face or Person detection
    public static boolean ShouldDetectFace = false;
    public static boolean ShouldDetectPerson = false;
    // Tracking or not
    public static boolean trackingEnable = false;


    //Requested speed
    private int yesSpeed = 30;
    //motor response
    private String YesMvtAck = "YesMove";

    //BI
    public static long lastBITime = 0;
    public static int voiceVolume = 150;

    private long startTime;
    final double THRESHOLD = 0.75;

    int previous_step=0;

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
                Log.i(name, "current step: " + step_num + "  ");
                // update
                previous_step = step_num;
            } // end if step = same


            // which grafcet step?
            switch (step_num) {
                case 0: // Wait for checkbox
                    //wait until check box
                    if (go) {
                        // go to next step
                        step_num = 1;
                    }
                    break;

                case 1: // Start Init Grafcet (Motors, STT, TTS)
                    InitGrafcet.go = true;
                    // go to next step
                    step_num = 2;
                    break;

                case 2 : // Wait for end of Init
                    if (!InitGrafcet.go) {
                        MainGrafcet.lastBITime = System.currentTimeMillis();
                        step_num = 3;
                    }
                    break;


                case 3: // Start LookingforSomeone Grafcet
                    BuddySDK.UI.setFacialExpression(FacialExpression.NEUTRAL);
                    LookingForSomeone.go = true;

                    // go to next step
                    step_num = 4;
                    break;



                case 4 : // Wait for end of LookingforSomeone Grafcet
                    if (!LookingForSomeone.go)
                        step_num = 10;
                    break;

                case 10: // Start ComeHere Grafcet
                    // start Tracking
                    ComeHere.go = true;
                    step_num = 15;
                    break;

                case 15 : // Wait for end of ComeHere Grafcet
                    if (!ComeHere.go)
                        step_num = 20;
                    break;

                case 20: // Start Interact Grafcet
                    // start Tracking
                    Interact.go = true;
                    step_num = 25;
                    break;

                case 25 : // Wait for end of Interact Grafcet
                    if (!Interact.go)
                        step_num = 20;
                    break;

                default :
                    // go to next step
                    step_num = 0;
                    break;
            } //End switch

            } // end try
            catch (Exception e) {
                Log.e(name, Log.getStackTraceString(e));
            }


        } // end run
    }; // end new runnable




}
