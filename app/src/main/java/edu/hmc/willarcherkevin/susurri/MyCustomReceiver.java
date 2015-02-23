package edu.hmc.willarcherkevin.susurri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    ChatActivity activity;

    public MyCustomReceiver(ChatActivity a){
        super();
        activity = a;
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


