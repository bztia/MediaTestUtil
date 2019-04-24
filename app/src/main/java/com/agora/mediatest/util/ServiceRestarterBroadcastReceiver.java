package com.agora.mediatest.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestarterBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaTest.Util.Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "ServiceRestarterBroadcastReceiver.onReceive()");
        context.startService(new Intent(context, TimeSyncService.class));
    }
}
