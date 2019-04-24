package com.agora.mediatest.util;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class TimeSyncService extends Service {

    static final int mPort = 12000;
    private static final String TAG = "MediaTest.Util.Service";
    public int mCounter = 0;
    ServerSocket mServerSocket;
    private Context mContext = null;
    private Thread mWorkThread;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public TimeSyncService(Context applicationContext) {
        super();
        Log.i(TAG, "TimeSyncService(Context)");
        mContext = applicationContext;
    }

    public TimeSyncService() {
        Log.i(TAG, "TimeSyncService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        mWorkThread = new Thread(new TimeSyncThread());
        mWorkThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");
        startTimer();
        return START_STICKY;
    }

    public void startTimer() {
        Log.i(TAG, "startTimer()");
        mTimer = new Timer();
        initializeTimerTask();
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        Log.i(TAG, "initializeTimerTask()");
        mTimerTask = new TimerTask() {
            public void run() {
                Log.i(TAG, "mCounter=" + (mCounter++));
            }
        };
    }

    public void stopTimerTask() {
        Log.i(TAG, "stopTimerTask");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service.onDestroy()");
        mWorkThread.interrupt();
        Intent broadcastIntent = new Intent(this, ServiceRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TimeSyncThread extends Thread {

        @Override
        public void run() {
            Log.i(TAG, "TimeSyncThread.run()");
            while (!isInterrupted()) {
                try {
                    mServerSocket = new ServerSocket(mPort);
                    while (!isInterrupted()) {
                        Socket socket = mServerSocket.accept();
                        Log.i(TAG, "TimeSyncThread.accept()");
                        DataInputStream dataInputStream =
                                new DataInputStream(socket.getInputStream());
                        DataOutputStream dataOutputStream =
                                new DataOutputStream(socket.getOutputStream());
                        long time_val = dataInputStream.readLong();
                        dataOutputStream.writeLong(System.currentTimeMillis());
                        socket.close();
                    }
                    Log.i(TAG, "TimeSyncThread.mServerSocket.close()");
                    mServerSocket.close();

                } catch (IOException e) {
                    Log.i(TAG, "TimeSyncThread.IOException1");
                    e.printStackTrace();
                }
            }
        }
    }
}
