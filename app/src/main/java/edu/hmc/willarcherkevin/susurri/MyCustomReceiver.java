package edu.hmc.willarcherkevin.susurri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    boolean updateNow = false;

    public boolean needUpdate(boolean updateNow) {
        return updateNow;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
        {
            Log.d(TAG, "Receiver intent null");
        }
        else
        {
            String action = intent.getAction();
            Log.d(TAG, "HELLLOOOO");
            updateNow = true;

        }
    }

}


