package com.example.phnx.dmote;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Josiah on 12/5/2015.
 */

public class connectServiceAsync extends AsyncTask<String, String, TCPClient>   {
    Handler mainUIHandler;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient;
    private ProgressDialog mProgressDialog;
    Handler handler;
    Runnable callback;
    private Activity activity;
    private String Prev;
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification noti;
    private NotificationManager nm;
    PendingIntent contentIntent;
    OnTest mCaller;
    TcpInterface mTcp;
    private boolean Disc,TimerOn1,TimerOn2,NotiAcce2,ReturningSound,DisconnectOnOff;
    private AsyncInterface sendMessage;
    TcpInterface one;
    boolean statusreturn;

    int mId = 1,tempInt;
    String temp;
    String status;

    connectServiceAsync(OnTest tcp){
         mCaller = tcp;

    }



    public void BindInterface (){
    sendMessage= null;

    }
    public String receiveCurrentStatus(){
        if(sendMessage!=null){

        }
        return status;
    }
    /*
    public void Vib(){
        if(((MainActivity)activity).getBool("Vibrate")){
        Vibrator v = (Vibrator) (((MainActivity) activity).getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE));
            if (v.hasVibrator()) {
                v.vibrate(400);
            }
            Vibrator k = (Vibrator) (((MainActivity) activity).getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE));
        }
    }

    public void playAlert(){
        if (((MainActivity)activity).getBool("Sound")) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/suctwotone");
                //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(((MainActivity) activity).getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }
        }
    }
    */
    /*
    public void playSuccess(){
        if (((MainActivity)activity).getBool("Sound")) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/success");
                Ringtone r = RingtoneManager.getRingtone(((MainActivity) activity).getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }
        }
    }

    public void playError(){
        if (((MainActivity)activity).getBool("Sound")) {
            try {
                Uri notification =  Uri.parse("android.resource://com.example.phnx.dmote/raw/computererror");
                Ringtone r = RingtoneManager.getRingtone(((MainActivity) activity).getApplicationContext(), notification);
                r.play();

            } catch (Exception e) {
                Log.e("sound", "ConnectTask: BURH");
            }
        }
    }
    public String convTImer(String val) {
        if(val.substring(1).equals("Looking for match")||val.substring(1).equals("Game Found")){return  val;}
        Log.e("ConvTimer","ConvTimer:"+val);
        if(val.length() > 3)
        {
            temp = val.substring(1);

            tempInt = 44 - (Integer.parseInt(temp) / 1000);
            temp = "Time Remaining: "+Integer.toString(tempInt)+"s";
            Log.e("ConvTimer","Temp:"+temp);
            return temp;

        }
        else{
            return val;
        }

    }
    */
    public void sendmess(String val){
            if (mTcpClient != null  ) {
            Log.e("Service",val+" Sent!");
            mTcpClient.sendMessage(val);
        }
    }
    public void mSendMessage(String val ){
        if(sendMessage!=null){
            sendMessage.onReceive(val);
        }

    }
    public connectServiceAsync()
    {


    }
    public void stopCl(){
        Log.e("TCP Client", "ConnectTask: stopCl");
        if (mTcpClient!=null){
        mTcpClient.stopClient();
        }
    }
    public void onAttach( Activity activity) {
        this.activity = activity;
        Log.e("TCP Client", "ConnectTask: Attached");
    }
    public void onDetach() {
        activity = null;
        Log.e("TCP Client", "ConnectTask: Detached");
    }

    @Override
    protected TCPClient doInBackground(String... message) {
        //we create a TCPClient object and
        mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
            {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //Log.e("TCP Client", "Message Received: ="+message);
                     /*
                    if(message.equals("Error"))
                    {
                        Log.e("TCP Client", "Disconnect RIGHT NOW");
                       // ConnectionBox = (TextView) findViewById(R.id.IsitConnected);

                        String ConnectChange= "Not Connected";
                        //ConnectionBox.setText(R.string.Disconnect);
                       // publishProgress(message);
                    }
                    else {
                       */

               // Log.e("TCP Client", "Async: " + message);

                //this method calls the onProgressUpdate
                    //  publishProgress(message);
                mCaller.returnMessage(message);
                //}
            }
                @Override
                public void returnConnectStatusA(boolean status){
                    statusreturn=status;
                    mCaller.returnConnectB(status);
                }
                @Override
            public void onConnect2(String message)   {
                Log.e("Service", "Connect2: "+message);
                mCaller.returnError(message);
                statusreturn=false;
                mCaller.returnConnectB(false);
            }


            }
        );
     //   mTcpClient.setTCPchec(one);
        mTcpClient.run(message[0], message[1]);

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {

        super.onProgressUpdate(values);
        /*
        if(Prev == null){
            Prev=values[0];
        }
        else {
            if(!Prev.equals(values[0])) {
                if (values[0].length() > 2) {
                    Disc = false;
                }
        */
                //in the arrayList we add the messaged received from server
            /*
            Log.e("TCP Client",LastRec);
            if(LastRec.isEmpty())
            {
                LastRec=values[0];
            }

            else
            {
                if (LastRec!=values[0])
                {
                    LastRec=values[0];
                }
            }
            Log.e("TCP Client",LastRec);

            */

        /*
                if (activity == null) {
                    Log.e("TCP", "ConnectTask:Skipped");
                } else {
                    Log.e("TCP Client", "ConnectTask: Message:" + values[0] + "<  >" + "Substring(1)= " + values[0].substring(1) + "Sound: " + ((MainActivity) activity).getBool("Sound"));
                    if (values[0].equals("Connected") || values[0].substring(1).equals("Connected")) {
                        ((MainActivity) activity).changeBool("Running", true);
                        ((MainActivity) activity).setVisibility("Connect", "Off");
                        if(!DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "On");
                            DisconnectOnOff=true;
                        }
                        ((MainActivity) activity).changeBool("createNotiReady", true);
                        ((MainActivity) activity).BoxChange("Connected");
                        //  ((MainActivity)activity).createNoti();
                        ReturningSound = false;
                    }

                    if (values[0].equals("Looking for Server") || values[0].substring(1).equals("Looking for Server")) {
                        ((MainActivity) activity).changeBool("Ongoing", true);
                    }
                    if (values[0].equals("Took too long to Accept") || values[0].substring(1).equals("Took too long to Accept")) {
                        Vib();
                        playError();
                        if(DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "Off");
                            DisconnectOnOff=false;
                        }
                        ((MainActivity) activity).setVisibility("Connect", "On");
                        Log.e("TCP", "ConnectTask:Took too long Disc" + Disc);
                        Disc = true;
                        TimerOn1 = false;
                        TimerOn2 = false;
                        ((MainActivity) activity).setVisibility("Accept", "Off");
                        ((MainActivity) activity).setVisibility("Decline", "Off");
                        stopCl();
                    }

                    if (values[0].substring(1).equals("Ingame")) {
                        Vib();
                        playSuccess();
                        Disc = true;
                    }

                    if (values[0].substring(1).equals("Declined")) {
                        ((MainActivity) activity).changeBool("Ongoing", false);

                        if(DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "Off");
                            DisconnectOnOff=false;
                        }
                        ((MainActivity) activity).setVisibility("Connect", "On");
                        TimerOn1 = false;
                        TimerOn2 = false;
                        Disc = true;
                        mTcpClient.stopClient();

                    }
                    if (values[0].substring(1).equals("Looking for match")) {
                        Log.e("TCP Client", "ConnectTask: Looking for Match: " + ReturningSound);
                        ((MainActivity) activity).changeBool("Ongoing", true);
                        //if the seaarch every returns to "Looking for match"
                        if (ReturningSound) {
                            playError();
                        }
                        if (!ReturningSound) {
                            ReturningSound = true;
                        }

                    }
                    //Sound Alert
                    if (values[0].substring(1).equals("Game Found") || values[0].equals("Game Found")) {

                    }
                    //Turns Buttons off and TimerOFF
                    if (values[0].equals("Checking if all accepted")) {
                        TimerOn1 = false;
                        TimerOn2 = false;
                        // Log.e("TCP Client", "ChoiceChangedtoNVM");
                        ((MainActivity) activity).stringchange("Nvm");
                        mTcpClient.sendMessage(((MainActivity) activity).getChoice());
                        ((MainActivity) activity).setVisibility("Accept", "Off");
                        ((MainActivity) activity).setVisibility("Decline", "Off");
                        if(!DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "On");
                            DisconnectOnOff=true;
                        }

                    }

                    //NotiAcce
                    if (values[0].substring(1).equals("Make Choice") || values[0].equals("Make Choice")) {
                        if(DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "Off");
                            DisconnectOnOff=false;
                        }
                        ((MainActivity) activity).setVisibility("Accept", "On");
                        ((MainActivity) activity).setVisibility("Decline", "On");
                        Vib();
                        TimerOn1 = true;
                        ((MainActivity) activity).changeBool("NotiAcce", true);

                        Log.e("TCP Client", "ConnectTask: ChoiceReady= " + ((MainActivity) activity).getChoice());
                        ((MainActivity) activity).changeBool("ChoiceReady", true);
                        //ChoiceReady = true;
                        playAlert();
                    }
                    //NotiAcce
                    if (values[0].equals("Checking if all accepted") || values[0].equals("Disconnected") || values[0].substring(1).equals("Disconnected") || values[0].equals("Looking for match") || values[0].substring(1).equals("Looking for match") || values[0].length() < 3) {
                        ((MainActivity) activity).changeBool("Ongoing", false);
                        ((MainActivity) activity).changeBool("NotiAcce", false);
                    }

                    //Ongoing
                    if (values[0].equals("Disconnected") || values[0].substring(1).equals("Ingame") || values[0].substring(1).equals("Declined") || values[0].substring(1).equals("Disconnected")) {
                        if(DisconnectOnOff) {
                            ((MainActivity) activity).setVisibility("Disconnect", "Off");
                            DisconnectOnOff=false;
                        }
                        ((MainActivity) activity).setVisibility("Connect", "On");

                        ((MainActivity) activity).changeBool("Ongoing", false);
                    }

                    if (values[0].length() > 2) {
                        if (TimerOn2) {
                            ((MainActivity) activity).addarray(convTImer(values[0]));
                            ((MainActivity) activity).updateNoti(convTImer(values[0]));

                        } else {
                            ((MainActivity) activity).addarray(values[0]);
                            ((MainActivity) activity).updateNoti(values[0]);
                        }
                    }
                    if (TimerOn1) {
                        TimerOn2 = true;
                    }
                    // Log.e("TCP Client", ((MainActivity)activity).getChoice() );
                    ((MainActivity) activity).notifyAd();
                //    mAdapter.notifyDataSetChanged();
                    Prev=values[0];

                    mCaller.returnMessage(values[0]);
                    Log.e("Service","Message:"+values[0]);
                    mSendMessage(values[0]);
                  //  sendmess("hi");

                }

            }
    */
        }




    @Override
    protected void onPostExecute(TCPClient meme)
    {
        /*
        if (activity != null) {
            Log.e("TCP Client", "ConnectTask: OnPostExecute"+ "Disc:"+Disc);

            ((MainActivity)activity).BoxChange("Disconnected");
            ((MainActivity)activity).setVisibility("Disconnect", "Off");
            //ConnectionBox.setText(R.string.Disconnect);

            if(!Disc && ((MainActivity) activity).getBool("Running") ) {
                Vib();
                ((MainActivity) activity).addarray("Disconnected");
                ((MainActivity) activity).updateNoti("Disconnected");
                playError();
            }

            ((MainActivity)activity).changeBool("Ongoing",false);

            ((MainActivity)activity).changeBool("NotiAcce",false);
            ((MainActivity) activity).changeBool("createNotiReady",false);
            ((MainActivity)activity).notifyAd();

            if (((MainActivity)activity).getReady()) {
                if (((MainActivity)activity).isAcceptVisible()) {
                    ((MainActivity)activity).setVisibility("Accept","Off");
                    ((MainActivity)activity).setVisibility("Decline","Off");
                    }

              }
            ((MainActivity)activity).setVisibility("Connect","On");
            Log.e("TCP Client", "ConnectTask: OnPostExecute"+ "end:"+true);
            if(((MainActivity)activity).getBool("Running")) {
                ((MainActivity) activity).changeBool("End", true);
                ((MainActivity)activity).changeBool("Running",false);
                stopCl();
            }
        }
        */
    }

    public interface OnTest
    {
        public void returnError(String error);
        public void returnMessage(String message);
        public void returnConnectB(boolean status);

    }


}