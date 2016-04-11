package com.example.phnx.dmote;

/**
 * Created by Josiah on 3/29/2016.
 * Use by the the components to send intents to the Service. Starting the Service,  Accept, Decline, and Disconnect Buttons.
 */
public class Constants {
    public interface ACTION {

    public static String STARTFOREGROUND_ACTION = "com.example.phnx.dmote.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.example.phnx.dmote.action.stopforeground";
        public static String ACCEPT_ACTION ="com.example.phnx.dmote.action.Accept";
        public static String DECLINE_ACTION ="com.example.phxn.dmote.action.Decline";
        public static String DISCONNECT_ACTION ="com.example.phxn.dmote.action.Disconnect";
    }

public interface NOTIFICATION_ID {
    public static int FOREGROUND_SERVICE = 101;
    public static int FOREGROUND_SERVICE2 = 202;
}
}
