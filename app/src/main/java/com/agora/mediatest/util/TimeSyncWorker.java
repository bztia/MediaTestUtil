package com.agora.mediatest.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeSyncWorker extends Worker {

    private static boolean mRunFlag = true;
    private static final int mPort = 12000;
    private static ServerSocket mServerSocket;
    private static final String TAG = "TimeSyncWorker";

    public TimeSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        while (mRunFlag) {
            try {
                mServerSocket = new ServerSocket(mPort);
                while (mServerSocket.isBound()) {
                    Socket socket = mServerSocket.accept();
                    Log.i(TAG, "mServerSocket.accept()");
                    DataInputStream dataInputStream =
                            new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream =
                            new DataOutputStream(socket.getOutputStream());
                    long time_val = dataInputStream.readLong();
                    dataOutputStream.writeLong(System.currentTimeMillis());
                    socket.close();
                }
                mServerSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "TimeSyncThread.IOException");
                e.printStackTrace();
            }
        }
        return Result.success();
    }
}
