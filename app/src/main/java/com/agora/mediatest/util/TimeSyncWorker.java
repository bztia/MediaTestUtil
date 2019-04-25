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

    private static final int mPort = 12000;
    private static ServerSocket mServerSocket = null;
    private static final String TAG = "TimeSyncWorker";

    public static boolean closeServer() {
        boolean status = mServerSocket.isClosed();
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public TimeSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.i(TAG, "mServerSocket.create()");
            mServerSocket = new ServerSocket(mPort);
            try {
                while (!mServerSocket.isClosed()) {
                    Socket socket = mServerSocket.accept();
                    Log.i(TAG, "mServerSocket.accept()");
                    DataInputStream dataInputStream =
                            new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream =
                            new DataOutputStream(socket.getOutputStream());
                    long data = dataInputStream.readLong();
                    dataOutputStream.writeLong(System.currentTimeMillis());
                    socket.close();
                    if (data == 0)
                    {
                        Log.i(TAG, "mServerSocket.break()");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i(TAG, "mServerSocket.close()");
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "TimeSyncWorker.finished()");
        return Result.success();
    }
}
