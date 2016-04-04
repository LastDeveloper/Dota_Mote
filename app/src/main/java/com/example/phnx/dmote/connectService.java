package com.example.phnx.dmote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Josiah on 3/24/2016.
 */
public class connectService extends Service  {
    /** indicates how to behave if the service is killed */
    int mStartMode;
    private int connection ;
    AsyncInterface mCallBack;
    TcpInterface mTcp;
    String Dmessage;
    private boolean unBound, running = false,notiOn = false;
    private final IBinder mBinder = new LocalBinder();
    /** interface for clients that bind */
    //IBinder mBinder;
    connectServiceAsync mAsyncTask;
    Service CS=this;
    DmoteNotification Please = new DmoteNotification(this);
    MainListener mMainListener;

    public class LocalBinder extends Binder {
        connectService getService() {
            // Return this instance of LocalService so clients can call public methods
            return connectService.this;
        }
        public void setListener(MainListener listener){
            mMainListener = listener;
        }

    }

    public void attachInterface(MainListener listener){
        mMainListener= listener;

    }
    public String getMessage() {
        if(Dmessage!=null){

            return Dmessage;
        }
        return "Wait!";
    }

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

        /** Called when the service is being created. */
        @Override
        public void onCreate() {
            Log.e ("Service","OnCreate");



        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            //Receive Ip Address
            Log.e("Service", "onStartCommand " );

            if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION) ) {
                Bundle extras = intent.getExtras();
                String ip_address = extras.getString("ip_address");
                String port = extras.getString("port");
                Log.e("Service", "     Ip Address:" + ip_address + "  Port: " + port);
                mAsyncTask = new connectServiceAsync(new connectServiceAsync.OnTest() {
                    @Override
                    public void returnError(String error) {
                        Log.e("Message", "Error:" + error);
                        if (error.equals("error")) {
                            mAsyncTask.stopCl();
                            stopForeground(true);
                            stopSelf();
                            running=false;
                        }
                    }

                    @Override
                    public void returnMessage(String message) {



                        if(!running) {
                            if(mMainListener!=null){
                                mMainListener.getServiceMessage(message);
                            }
                            Log.e("Message", "Message:" + message);
                            Please.returnNotfication(message);
                            notiOn= true;
                           // createNotification();
                            running = true;
                        }
                        else if(message.equals("Disconnect")||message.substring(1).equals("Disconnect")){
                            Please.updateNotification(message);
                            if(mMainListener!=null){
                                mMainListener.getServiceMessage(message);
                            }
                            running=false;
                            Log.e("Message", "STOP: " + message);
                            mAsyncTask.stopCl();
                            stopForeground(true);
                            stopSelf();
                            running=false;
                        }
                        else if (notiOn &&!message.equals(Dmessage)){
                            if(mMainListener!=null){
                                mMainListener.getServiceMessage(message);
                            }
                            Log.e("Message", "Message:" + message);
                            Please.updateNotification(message);
                            // updateNotification(message);
                        }

                        Dmessage = message;

                    }

                    @Override
                    public void returnConnectB(boolean status) {
                        Log.e("Connection", "Status: " + status);
                    }
                });
                mAsyncTask.execute(ip_address, port);
                //mAsyncTask.execute(ip_address,port);
            }
            else if(intent.getAction().equals(Constants.ACTION.ACCEPT_ACTION)){
                Log.e("Service","Send Mess Attempt: Accept");
                mAsyncTask.sendmess("Accept");
            }
            else if(intent.getAction().equals(Constants.ACTION.DECLINE_ACTION)){
                Log.e("Service","Send Mess Attempt: Decline");
                mAsyncTask.sendmess("Decline");
            }


            return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        Log.e ("Service","onBind");
       // mAsyncTask.BindInterface();
       // mAsyncTask.receiveCurrentStatus();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        if(mMainListener!=null && running){
            mMainListener.getServiceMessage(Dmessage);
        }
        Log.e ("Service","onRebind");
      //  mAsyncTask.BindInterface();
       // mAsyncTask.receiveCurrentStatus();

    }
    @Override
    public boolean onUnbind(Intent intent){
        Log.e("Service", "connectService: onUnbind " );
        return true;
    }

    @Override
    public void onDestroy() {
        Log.e ("Service","onDestroy");

    }

    public void createNotification(){
        notiOn= true;

        Notification notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                        .setContentTitle("DotaMote- Remote Matchmaking")
                        .setContentText(Dmessage)
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MAX).build();
       // if(Dmessage)
          //  mBuilder.addAction(R.drawable.greya_24dp,"Accept",resultPendingIntent).addAction(R.drawable.greyd_24dp,"Decline",resultPendingIntent2);
       // notification.build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);


       // NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       // mNotificationManager.notify(Tag,o, mBuilder.build());
    }
    public void updateNotification(String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentText(message)
                .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                .setContentTitle("DotaMote- Remote Matchmaking")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());
    }
    public void deleteNotification(String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentText(message)
                .setSmallIcon(R.drawable.greysmalliconfilled24dp)
                .setContentTitle("DotaMote- Remote Matchmaking")
                .setOngoing(false)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());
    }

    public interface MainListener{

        public void getServiceMessage(String message);

    }


}


