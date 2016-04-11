package com.example.phnx.dmote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Josiah on 3/24/2016.
 */
public class connectService extends Service  {
    /** indicates how to behave if the service is killed */
    int mStartMode;
    String Dmessage;
    private boolean unBound, running = false,notiOn = false;
    private final IBinder mBinder = new LocalBinder();
    /** interface for clients that bind */
    connectServiceAsync mAsyncTask;

    customNotifcation Please = new customNotifcation(this);
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
                        if( error.equals("End")  ){
                            if(Dmessage!=null) {
                                Log.e("Message", "      |"+Dmessage);
                                Log.e("Message", "      |"+Dmessage.substring(1)+"    "+Dmessage.substring(2));
                                if(!Dmessage.equals("Disconnect") && !Dmessage.substring(1).equals("Disconnect") ) {
                                    mMainListener.getServiceMessage("_Disconnect");
                                    mAsyncTask.stopCl();
                                    stopForeground(true);
                                    stopSelf();
                                    running = false;
                                }
                            }
                        }
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
                            running = true;

                        }
                        else if(message.equals("Disconnect")||message.substring(1).equals("Disconnect")&&running){
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
                            if(message.equals("Checking...") || message.substring(1).equals("Checking...")){
                                mAsyncTask.sendmess("Reset");

                            }
                            if(mMainListener!=null){
                                mMainListener.getServiceMessage(message);

                            }
                            Log.e("Message", "Message:" + message);
                            Please.updateNotification(message);

                        }
                        Dmessage = message;

                    }

                    @Override
                    public void returnConnectB(boolean status) {
                        Log.e("Connection", "Status: " + status);
                    }
                });
                mAsyncTask.execute(ip_address, port);

            }

            else if(intent.getAction().equals(Constants.ACTION.ACCEPT_ACTION)){
                Log.e("Service","Send Message: Accept");
                mAsyncTask.sendmess("Accept");

            }
            else if(intent.getAction().equals(Constants.ACTION.DECLINE_ACTION)){
                Log.e("Service","Send Message: Decline");
                mAsyncTask.sendmess("Decline");

            }
            else if(intent.getAction().equals(Constants.ACTION.DISCONNECT_ACTION)){
                Log.e("Service", " Send Message: Disconnect");

                if (mAsyncTask!=null) {  Log.e("Service", "     mAsynctask.getRunning: "+mAsyncTask.getRunning());
                    if (mAsyncTask.getRunning()) {
                        mAsyncTask.stopCl();
                        stopSelf();
                        stopForeground(true);
                    }
                    else
                    {
                        Please.cancel();
                    }
                }
                else {
                    Please.cancel();
                }

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


    public interface MainListener{

        public void getServiceMessage(String message);

    }


}


