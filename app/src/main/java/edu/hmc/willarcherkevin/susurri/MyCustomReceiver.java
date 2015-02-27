package edu.hmc.willarcherkevin.susurri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    ChatActivity activity;
    String room;

    public MyCustomReceiver(ChatActivity a, String r){
        super();
        activity = a;
        room = r;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
        {
            Log.d(TAG, "Receiver intent null");
        }
        else
        {
            activity.updateChat();
        }
    }

}


