package com.example.phnx.dmote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Josiah on 3/29/2016.
 */
public class DmoteNotification {
    private Service mService;
//                                       0              1                2               3                4                    5                   6                  7          8        9                         10                   11
    private String StateArray[] = {"Connected","Looking For Match","Game Found", "Accept Or Decline","Checking...","Looking For Server","Checking If In Game...","In Game","Declined","Failed To Ready Up","Returning To Search...","Disconnect"};
    ArrayList<String> StateList = new ArrayList<String>(Arrays.asList(StateArray));
    private  Intent AcceptIntent;
    private  Intent DeclineIntent;
    private  PendingIntent AcceptPendingIntent;
    private PendingIntent DeclinePendingIntent;
    private SharedPreferences settings;
    String LastValue;


    DmoteNotification(Service service){


        mService= service;

     //   settings = service.getDefaultSharedPreferencesName(service);

    }



    public void Vib(){
        settings = PreferenceManager.getDefaultSharedPreferences(mService);
        if (settings.getBoolean("Vibrate",false)){
        Vibrator v = (Vibrator) mService.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                v.vibrate(400);
            }
            Vibrator k = (Vibrator) mService.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
    public void playAlert(){
        settings = PreferenceManager.getDefaultSharedPreferences(mService);
        if (settings.getBoolean("Sound Alert",false)) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/suctwotone");
                //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(mService.getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }
        }
    }
    public void playSuccess(){
        settings = PreferenceManager.getDefaultSharedPreferences(mService);
        if (settings.getBoolean("Sound Alert",false)) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/success");
                Ringtone r = RingtoneManager.getRingtone(mService.getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }

        }
    }

    public void playError(){
        settings = PreferenceManager.getDefaultSharedPreferences(mService);
        if (settings.getBoolean("Sound Alert",false)) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/computererror");
                Ringtone r = RingtoneManager.getRingtone(mService.getApplicationContext(), notification);
                r.play();

            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }
        }
    }

    private int getPhaseInt(String state){
     //   Log.e(" Notification STATE", "STATE CHECK: "+state+ "  Sub: "+state.substring(1) );

        if(state.length()>2) {
     //       Log.e(" Notification STATE", "    "+Integer.toString(StateList.indexOf(state)));
            return StateList.indexOf(state);
        }
        return -1;
    }

    public void returnNotfication ( String initial_message){



        int state_number = getPhaseInt(initial_message);

        AcceptIntent = new Intent(mService,connectService.class);
         DeclineIntent = new Intent(mService,connectService.class);

        //Put Accept or Decline inside the intents, so when the Notification Action is pressed, it sill Sent Accept
        AcceptIntent.setAction(Constants.ACTION.ACCEPT_ACTION);
        DeclineIntent.setAction(Constants.ACTION.DECLINE_ACTION);

        //Initialize Pending Intents http://developer.android.com/intl/zh-cn/reference/android/app/PendingIntent.html
         AcceptPendingIntent = PendingIntent.getService(mService, 0,
                AcceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        DeclinePendingIntent = PendingIntent.getService(mService, 0,
                DeclineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        if(initial_message.contains("Time")) {
            state_number = 3;
        }

        switch (state_number){
            case 0:case 1:case 2:case 4:case 5:case 6:case 7:case 8:case 9:case 11:
        //        Log.e("Create Notification", "CASE 1-9 !3 : ACCEPT OR DECLINE");
              //  NotificationCompat.Builder builder = new NotificationCompat.Builder(mService);
                Notification notification = new android.support.v4.app.NotificationCompat.Builder(mService)
                        .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                        .setContentTitle("DotaMote- Remote Matchmaking")
                        .setContentText(initial_message)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX).build();
                mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);
                break;
            case 10:
            Notification notificationError = new android.support.v4.app.NotificationCompat.Builder(mService)
                    .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                    .setContentTitle("DotaMote- Remote Matchmaking")
                    .setContentText(initial_message)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX).build();
            mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notificationError);
            break;



            case 3:
        //        Log.e("Create Notification", "CASE 3: ACCEPT OR DECLINE");

                Notification NotifcationWithButtons = new android.support.v4.app.NotificationCompat.Builder(mService)
                        .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                        .setContentTitle("DotaMote- Remote Matchmaking")
                        .setContentText(initial_message)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .addAction(R.drawable.greya_24dp,"Accept",AcceptPendingIntent)
                        .addAction(R.drawable.greyd_24dp,"Decline",DeclinePendingIntent).build();

                mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        NotifcationWithButtons);
                Vib();
                playAlert();


                break;




        }

            Notification notification = new android.support.v4.app.NotificationCompat.Builder(mService)
                    .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                    .setContentTitle("DotaMote- Remote Matchmaking")
                    .setContentText(initial_message)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX).build();
        mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);


    }

    public void updateNotification(String state){
    //    Log.e(" Notification Glacier", "Update: " + state + "   Sub: "+state.substring(1));
        int state_number = -1;
        if(state.length()>2) {
            state_number = getPhaseInt(state.substring(1));
        }
    //    Log.e(" Notification Glacier", "    "+Integer.toString(state_number));
      //  switch (state_number){
        if(state_number>=0 && state_number<11 ) {
           // case 0:case 1:case 4:case 5:case 6:case 10:
         //   {
    //        Log.e(" Notification Glacier", "    normal");

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService)
                        .setContentText(state)
                        .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                        .setContentTitle("DotaMote- Remote Matchmaking")
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX);

                NotificationManager mNotificationManager =
                        (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);


                mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());
            LastValue = state;
            if(state_number == 10 || state_number == 9){
                playError();
                Vib();
            }
            if(state_number == 3 ){
                playAlert();
                Vib();
            }
            if(state_number == 7){
                playSuccess();
                Vib();
            }

        }

                else if(state_number==11) {
     //       Log.e(" Notification Glacier", "    " + Integer.toString(state_number) + "=11   " + " State: " + state + "LAstVal: "+LastValue);
                    if(getPhaseInt(LastValue.substring(1))==7 ||getPhaseInt(LastValue.substring(1))==8|| getPhaseInt(LastValue.substring(1))==9 ) {
    //                    Log.e(" Notification Glacier", "    7,8,9");
                        NotificationCompat.Builder mBuilder3 = new NotificationCompat.Builder(mService)
                                .setContentText(LastValue)
                                .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                                .setContentTitle("DotaMote- Remote Matchmaking")
                                .setOngoing(false)
                                .setPriority(Notification.PRIORITY_MAX);

                        NotificationManager mNotificationManager3 =
                                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager3.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE2, mBuilder3.build());
                    }
                    else {

      //                  Log.e(" Notification Glacier", "    Disconnect");

                        NotificationCompat.Builder mBuilder3 = new NotificationCompat.Builder(mService)
                                .setContentText(state)
                                .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                                .setContentTitle("DotaMote- Remote Matchmaking")
                                .setOngoing(false)
                                .setPriority(Notification.PRIORITY_MAX);

                        NotificationManager mNotificationManager3 =
                                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager3.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE2, mBuilder3.build());
                    }
                }


         else if (state.contains("Accept Or Decline")){
//            Log.e(" Notification Glacier", "         Contains");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService)
                    .setContentText(state)
                    .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                    .setContentTitle("DotaMote- Remote Matchmaking")
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .addAction(R.drawable.greya_24dp,"Accept",AcceptPendingIntent)
                    .addAction(R.drawable.greyd_24dp,"Decline",DeclinePendingIntent);


            NotificationManager mNotificationManager =
                    (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());
             LastValue=state;

        }

    }
}
