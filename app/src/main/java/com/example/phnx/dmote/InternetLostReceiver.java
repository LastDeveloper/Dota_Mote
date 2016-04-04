package com.example.phnx.dmote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Josiah on 12/7/2015.
 */

public class InternetLostReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String val = intent.getStringExtra("Test");
        Log.e( "Broad ",val);
        Intent tIntent = new Intent("MEME");
        tIntent.putExtra("test2",val);
        context.sendBroadcast(tIntent);
    }
}