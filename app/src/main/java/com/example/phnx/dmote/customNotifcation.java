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
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Josiah on 3/25/2016.
 * Logic for the Notifications , creation and Updating. Custom Notifications created using RemoteViews.
 * Buttons are set with PendingIntents that re-open the application, Accept or Decline the Match, or to Disconnect.
 */
public class customNotifcation   {
    /** Intialize the Intents, and only update the Status with a value matching the values in the State Arry*/
    private Service mService;
    private String StateArray[] = {"Connected","Looking For Match","Game Found", "Accept Or Decline","Checking...","Looking For Server","Checking If In Game...","In Game","Declined","Failed To Ready Up","Returning To Search...","Disconnect"};
    ArrayList<String> StateList = new ArrayList<String>(Arrays.asList(StateArray));
    private Intent AcceptIntent;
    private  Intent DeclineIntent, DisconnectIntent, MainIntent;
    private PendingIntent AcceptPendingIntent;
    private PendingIntent DeclinePendingIntent,DisconnectPendingIntent, MainPendingIntent;
    private RemoteViews mRemoteViews;
    private SharedPreferences settings;
    String LastValue;


    /** Creating an customNotification object with the Service passed to it. This is so customNotification can execute all of the
     * Notification with the passed Service Context.
     *
     */
    customNotifcation(Service service){

        mService= service;

    }



    /** Returns the matching State Index ( from StateArray[] ) depending on the message received from the Desktop Client */
    private int getPhaseInt(String state){

        if(state.length()>2) {

            return StateList.indexOf(state);
        }
        return -1;
    }


    /** The Initial creation of the Notification, when the Service is first connected to the Desktop Client */
    public void returnNotfication ( String initial_message){


        //Get the Intent State for the Switch Statement later;
        int state_number = getPhaseInt(initial_message);

        //Creation of Button Intents
        AcceptIntent = new Intent(mService,connectService.class);
        DeclineIntent = new Intent(mService,connectService.class);
        DisconnectIntent = new Intent(mService,connectService.class);


        /** MainIntent: Starts the MainActivity */
        MainIntent =  new Intent(mService, MainActivity.class);
        MainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



        /**Put Accept or Decline inside the intents, so when the Notification Action is pressed, it will Sent Accept */
        AcceptIntent.setAction(Constants.ACTION.ACCEPT_ACTION);
        DeclineIntent.setAction(Constants.ACTION.DECLINE_ACTION);
        DisconnectIntent.setAction(Constants.ACTION.DISCONNECT_ACTION);



        /**Initialize Pending Intents using the Intents, Pending Intents are necessary for the Notification Buttonshttp://developer.android.com/intl/zh-cn/reference/android/app/PendingIntent.html */
        MainPendingIntent = PendingIntent.getActivity(  mService,0,MainIntent ,0);

        AcceptPendingIntent = PendingIntent.getService(mService, 0,
                AcceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        DeclinePendingIntent = PendingIntent.getService(mService, 0,
                DeclineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        DisconnectPendingIntent = PendingIntent.getService(mService, 0,
                DisconnectIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        /** Certain States update the Notification Differently
         *
         * Custom Layout for the Notifications, Uses Remote Views , Intents have to be applied the Buttons in the Notification Layouts
         * Icons still must be set if using RemoteViews.
         *
         * **/
        Log.e("MAIN_ACTIVITY", "    return Case: " + initial_message);
        if(state_number!=-1) {
            switch (state_number) {
                /** Common states, Look at StateArray, only updates the text in the Notification  **/
                case 0:
                case 1:
                case 2:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 11:
                    mRemoteViews = new RemoteViews(mService.getPackageName(), R.layout.notification);
                    mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.dotamote_vector5);
                    mRemoteViews.setTextViewText(R.id.notif_content, initial_message);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationExitButton, DisconnectPendingIntent);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(mService)
                           // .setSmallIcon(R.drawable.dotamote_vector5)
                            .setSmallIcon(R.mipmap.dotamote_launcher)
                            .setContent(mRemoteViews)
                            .setContentTitle("Dotamote")
                            .setOngoing(true)

                            .setPriority(Notification.PRIORITY_MAX);
                    mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notification.build());
                    break;
                /** Failed to Ready Up, Play Error Sound */
                case 10:
                    mRemoteViews = new RemoteViews(mService.getPackageName(), R.layout.notification);
                    mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.dotamote_vector5);
                    mRemoteViews.setTextViewText(R.id.notif_content, initial_message);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationExitButton, DisconnectPendingIntent);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
                    NotificationCompat.Builder notificationError = new android.support.v4.app.NotificationCompat.Builder(mService)
                           // .setSmallIcon(R.drawable.dotamote_vector5)
                            .setSmallIcon(R.mipmap.dotamote_launcher)
                            .setContent(mRemoteViews)
                            .setOngoing(true)
                            .setPriority(Notification.PRIORITY_MAX);
                    mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            notificationError.build());
                    break;


                /**   Accept or Decline, Display Buttons and the Expanded Layout version (notification_buttons.xml) of the Notification  */
                case 3:

                    mRemoteViews = new RemoteViews(mService.getPackageName(), R.layout.notification_buttons);
                    mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.dotamote_vector5);
                    mRemoteViews.setTextViewText(R.id.notif_content, initial_message);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationAcceptButton, AcceptPendingIntent);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationDeclineButton, DeclinePendingIntent);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationExitButton, DisconnectPendingIntent);
                    mRemoteViews.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);

                    NotificationCompat.Builder NotifcationWithButtons = new NotificationCompat.Builder(mService)
                            .setSmallIcon(R.drawable.dotamote_vector5)
                            .setContent(mRemoteViews)
                            .setOngoing(true)
                            .setPriority(Notification.PRIORITY_MAX)
                            ;
                    mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                            NotifcationWithButtons.build());
                    Vib();
                    playAlert();


                    break;


            }
        }
        else if(initial_message.contains("Accept Or Decline")) {
            mRemoteViews = new RemoteViews(mService.getPackageName(), R.layout.notification);
            mRemoteViews.setImageViewResource(R.id.notif_icon, R.drawable.dotamote_vector5);
            mRemoteViews.setTextViewText(R.id.notif_content, initial_message);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationExitButton, DisconnectPendingIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(mService)
                    .setSmallIcon(R.drawable.dotamote_vector5)
                    .setContent(mRemoteViews)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX);
            mService.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification.build());
        }
    }

    /** Updating the Notification after is creation */
    public void updateNotification(String state){
        int state_number = -1;
        Log.e("MAIN_ACTIVITY", "    Update Case: " + state);

        /** Get State Index from the given state, Sometimes the messages have unknown characters in the beginning, so substring is used */
        if(state.length()>2) {
            state_number = getPhaseInt(state.substring(1));
        }

        /**Common update states for Notifirication, when common the last status is saved in LastValue, to be used on Disconnect   */
        if(state_number>=0 && state_number<11 ) {
            RemoteViews mRemoteViews2  = new RemoteViews(mService.getPackageName(),R.layout.notification );
            mRemoteViews2.setImageViewResource(R.id.notif_icon,R.drawable.dotamote_vector5);
            mRemoteViews2.setTextViewText(R.id.notif_content,state);
            mRemoteViews2.setOnClickPendingIntent(R.id.NotificationExitButton,DisconnectPendingIntent);
            mRemoteViews2.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService)
                  //  .setSmallIcon(R.drawable.dotamote_vector5)
                    .setSmallIcon(R.mipmap.dotamote_launcher)
                    .setContentTitle("Dmote?")
                    .setContentText("kek")
                    .setContent(mRemoteViews2)
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

        /**Updating the Notification on Disconnect */
        else if(state_number==11) {
            /** If the last game state (LastValue) is 'Declined', 'In Game', Or 'Failed To Ready Up', before a Disconnection, Don't change the Status   */
            if(getPhaseInt(LastValue.substring(1))==7 ||getPhaseInt(LastValue.substring(1))==8|| getPhaseInt(LastValue.substring(1))==9 ) {

                RemoteViews mRemoteViews2  = new RemoteViews(mService.getPackageName(),R.layout.notification );
                mRemoteViews2.setImageViewResource(R.id.notif_icon,R.drawable.dotamote_vector5);
                mRemoteViews2.setTextViewText(R.id.notif_content,LastValue);
                mRemoteViews2.setOnClickPendingIntent(R.id.NotificationExitButton,DisconnectPendingIntent);
                mRemoteViews2.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
                NotificationCompat.Builder mBuilder3 = new NotificationCompat.Builder(mService)
                        .setSmallIcon(R.drawable.dotamote_vector5)
                        .setContent(mRemoteViews2)
                        .setOngoing(false)
                        .setPriority(Notification.PRIORITY_MAX);

                NotificationManager mNotificationManager3 =
                        (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager3.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE2, mBuilder3.build());
            }
            /** Else, update the Status with 'Disconnect' on Disconnection */
            else {

                RemoteViews mRemoteViews2  = new RemoteViews(mService.getPackageName(),R.layout.notification );
                mRemoteViews2.setImageViewResource(R.id.notif_icon,R.drawable.dotamote_vector5);
                mRemoteViews2.setTextViewText(R.id.notif_content,state);
                mRemoteViews2.setOnClickPendingIntent(R.id.NotificationExitButton,DisconnectPendingIntent);
                mRemoteViews2.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
                NotificationCompat.Builder mBuilder3 = new NotificationCompat.Builder(mService)
                        .setSmallIcon(R.drawable.dotamote_vector5)
                        .setContent(mRemoteViews2)
                        .setOngoing(false)
                        .setPriority(Notification.PRIORITY_MAX);

                NotificationManager mNotificationManager3 =
                        (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager3.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE2, mBuilder3.build());
            }
        }

        /** Updating the notification with when the Accept or Decline timer */
        else if (state.contains("Accept Or Decline")){
            mRemoteViews  = new RemoteViews(mService.getPackageName(),R.layout.notification_buttons );
            mRemoteViews.setImageViewResource(R.id.notif_icon,R.drawable.dotamote_vector5);
            mRemoteViews.setTextViewText(R.id.notif_content,state);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationExitButton,DisconnectPendingIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationAcceptButton,AcceptPendingIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationDeclineButton,DeclinePendingIntent);
            mRemoteViews.setOnClickPendingIntent(R.id.NotificationMainButton, MainPendingIntent);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mService)
                    .setSmallIcon(R.drawable.dotamote_vector5)

                    .setContent(mRemoteViews)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX);


            Notification noti = mBuilder.build();
            /** Buttons are added but, the notification needs to be larger height  */
            noti.bigContentView = mRemoteViews;



            NotificationManager mNotificationManager =
                    (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, noti);
            LastValue=state;

        }

    }
    /** Canceling the Notification **/
    public void cancel(){
        NotificationManager mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE2);

    }

    /** Notification Alert methods, Vibrate, Playing of the Normal Alert Sound, Error Sound, and Success Sound */
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
                Log.e("sound", "Alert: Error");
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
                Log.e("sound", "Success:Error");
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
                Log.e("sound", "Error: Error");
            }
        }
    }

}
