package com.bfr.welcomeproto.grafcet;


import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.welcomeproto.utils.bfr_Grafcet;


import java.util.Locale;

public class InitGrafcet extends bfr_Grafcet {

    private static final String TAG = "GRAFCET";

    public InitGrafcet(String mname) {
        super(mname);
        this.grafcet_runnable = mysequence;
    }

    private InitGrafcet grafcet=this;

    // Static variable (to manage the grafcet from outside)
//    public static int step_num =0;
    public static boolean go = false;



//    private int previous_step = 0;
    public static int step_num = 0;
    public static int previous_step = 0;
    private double time_in_curr_step = 0;
    private boolean bypass = false;

    // Face or Person detection
    public static boolean ShouldDetectFace = false;
    // Tracking or not
    public static boolean trackingEnable = false;


    //Requested speed
    private int yesSpeed = 30;
    //motor response
    private String yesMvtAck = "YesMove";
    private String noMvtAck = "noMove";



    // Define the sequence/grafcet to be executed
    // runable for grafcet
    private Runnable mysequence = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                // if step changed
                if (!(step_num == previous_step)) {
                    // display current step
                    Log.d(name, "current step: " + step_num + "  ");
                    // update
                    previous_step = step_num;
                } // end if step = same


                // which grafcet step?
                switch (step_num) {
                    case 0: // Wait for start
                        if (go) {
                            // go to next step
                            step_num = 1;
//                        mBuddySDK.playEvent(gcontext, FacialEvent.FALL_ASLEEP, onFacialEvent);
                        }
                        break;

                    case 1: // Enable Yes
                        Log.d(name, "Enabling Yes: " + step_num + "  ");
                        BuddySDK.USB.enableYesMove(1, new IUsbCommadRsp.Stub() {
                            @Override
                            public void onSuccess(String success) throws RemoteException {
                                Log.i("enable No SUCESS:", success);
                            }

                            @Override
                            public void onFailed(String error) throws RemoteException {
                                Log.e("enable No FAILED", error);
                            }
                        });

                        // go to next step
                        step_num = 2;
                        break;

                    case 2: //wait for Yes to be enabled
                        // if no not disabled
                        if (!BuddySDK.Actuators.getYesStatus().toUpperCase().contains("DISABLE")) {
                            // go to next step
                            step_num = 3;
                        }
                        break;

                    case 3: // Enable No
                        BuddySDK.USB.enableNoMove(1, new IUsbCommadRsp.Stub() {
                            @Override
                            public void onSuccess(String success) throws RemoteException {
                                Log.i("enable No SUCESS:", success);
                            }

                            @Override
                            public void onFailed(String error) throws RemoteException {
                                Log.e("enable No FAILED", error);
                            }
                        });

                        // go to next step
                        step_num = 4;
                        break;

                    case 4: // Wait for No enable

                        // go to next step
                        if (!BuddySDK.Actuators.getNoStatus().toUpperCase().contains("DISABLE")) {
                            // go to next step
                            step_num = 5;
                        }
                        break;

                    case 5: // enable wheels
                        BuddySDK.USB.enableWheels(1, 1, new IUsbCommadRsp.Stub() {
                            @Override
                            public void onSuccess(String success) throws RemoteException {
                                Log.i("enable Wheels SUCESS:", success);
                            }

                            @Override
                            public void onFailed(String error) throws RemoteException {
                                Log.e("enable Wheels FAILED", error);
                            }
                        });

                        // go to next step
                        step_num = 6;
                        break;

                    case 6: // wait for left wheel to be enabled
                        // if no not disabled
                        if (!BuddySDK.Actuators.getLeftWheelStatus().toUpperCase().contains("DISABLE")) {
                            // go to next step
                            step_num = 7;
                        }
                        break;
                    case 7: // wait for right wheel to be enabled
                        // if no not disabled
                        if (!BuddySDK.Actuators.getRightWheelStatus().toUpperCase().contains("DISABLE")) {
                            // go to next step
                            step_num = 10;
                        }
                        break;

                    case 10: // init Yes at zero position
                       BuddySDK.USB.buddySayYes(40, 15, new IUsbCommadRsp.Stub() {
                           @Override
                           public void onSuccess(String s) throws RemoteException {
                               yesMvtAck=s;
                           }

                           @Override
                           public void onFailed(String s) throws RemoteException {

                           }
                       });
                        step_num=15;
                        break;

                    case 15: // wait for end of yes mvt
                    if (yesMvtAck.toUpperCase().contains("FINISHED"))
                    {
                        step_num=20;
                    }
                        break;

                    case 20: // init No at zero position
                        BuddySDK.USB.buddySayNo(40, 0, new IUsbCommadRsp.Stub() {
                            @Override
                            public void onSuccess(String s) throws RemoteException {
                                noMvtAck=s;
                            }

                            @Override
                            public void onFailed(String s) throws RemoteException {

                            }
                        });
                        step_num=25;
                        break;

                    case 25: // wait for end of no mvt
                        if (noMvtAck.toUpperCase().contains("FINISHED"))
                        {
                            step_num=99;
                        }
                        break;

                    case 99: // exit grafcet
//                    mBuddySDK.playEvent(gcontext, FacialEvent.AWAKE, onFacialEvent);
                        // reset
                        go = false;
                        step_num = 0;
                        break;

                    default:
                        // go to next step
                        step_num = 0;
                        break;
                } //End switch

            } catch (Exception e) {
                e.printStackTrace();
            }

        } // end run
    }; // end new runnable



}
