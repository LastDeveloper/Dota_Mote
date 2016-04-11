package com.example.phnx.dmote;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {

    private String serverMessage;
    public static final String SERVERIP = "192.168.2.72"; //your computer IP address
    public static String TestIP;
    public int foo;
    public static final int SERVERPORT = 12345;
    private OnMessageReceived mMessageListener = null;
    private TcpInterface mTCPListener = null  ;
    private boolean mRun = false;
    private String bae;
    PrintWriter out;
    BufferedReader in;


    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public  TCPClient(OnMessageReceived listener) {
        Log.e("Service","TCP OnmessageReceived Linser");
        mMessageListener = listener;
    }

  //  public void setTCPchec(TcpInterface listener) {
    //    Log.e( "setTCPchec: ", "welp" );
    //    mTCPListener = listener;
    //}

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {

            out.println(message);
            bae=message;
            out.flush();
        }
        else
        {
            Log.e("TCP Client", "SendElse");
            TestIP=message;
        }
    }

    public void stopClient(){

        mRun = false;
        Log.e("TCP Client", "TCPClient: stopClient()");
    }
    public boolean getmRun(){
        return mRun;

    }

    public void run(String message, String message2) {

        mRun = true;
        Log.e("TCP Client", "Message:= "+ message+" Port:"+message2);
        try {
            foo = Integer.parseInt(message2);
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(message);

            Log.e("TCP Client", "C: Connecting...");



            //create a socket to make the connection with the server
            Socket socket = new Socket();

            socket.connect(new InetSocketAddress(serverAddr,foo),2000);

         //   Socket socket = new Socket(serverAddr, foo);
            Log.e("TCP Client", "C: S...");
            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent."+" mRun:"+mRun);



                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server

                while (mRun) {
                    mMessageListener.returnConnectStatusA(true);
                   // mTCPListener.onConnect(1);
                    Log.e("TCP Client", "running");
                    serverMessage = in.readLine();
                    out.println(bae);
                   // Log.e("TCP Client", "C:"+bae);
                    if (serverMessage != null && mMessageListener != null) {
                        Log.e("TCP Client", "C:"+"hello?");
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;
                    Log.e("TCP Client", "C:"+"hello?2");


                }
                Log.e("TCP Client", "C:"+"three");
                Log.e("TCP Client", "TCPCLient: Try Over"+serverMessage);
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {
                String t = "error";
                mMessageListener.onConnect2(t);
                Log.e("Service", "S: Error");
               // mMessageListener.messageReceived("Error");
                //mErrorAdapter

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                mMessageListener.onConnect2("End");
                Log.e("Service", "TCPClient END");
                socket.close();
            }

        } catch (Exception e) {

               String t = "error";
               mMessageListener.onConnect2(t);

            Log.e("TCP", "C: Error", e);

        }

    }



    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
        public void onConnect2(String message);
        public void returnConnectStatusA(boolean status);
    }

}