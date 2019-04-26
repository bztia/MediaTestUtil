package com.agora.mediatest.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static android.view.View.FOCUS_LEFT;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MediaTestUtilActivity extends AppCompatActivity implements View.OnTouchListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private Handler mTimerHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private TextView mTimerText;
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private Button mIpAddressButton;
    //private static final String IMAGE_URL = "http://fansart.com/upload1/20171210/1512861746-0.jpg";
    //private static final String IMAGE_URL = "http://www.myjscode.com/images/earth1.jpg";
    //private static final String IMAGE_URL = "http://cons452.sites.olt.ubc.ca/files/2013/12/Colour_World_Banner-with-Credits-black.png";
    private static final String IMAGE_URL = "https://sum4all.org/data/files/styles/top_banner/public/page/banner/colour_world_banner.png";
    private int _xDelta;
    private int _yDelta;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private String getWlanIpAddress() {
        try {
            Enumeration<NetworkInterface> network_interfaces = NetworkInterface.getNetworkInterfaces();
            List<NetworkInterface> interfaces = Collections.list(network_interfaces);
            for (NetworkInterface inet : interfaces) {
                if (!inet.getName().equalsIgnoreCase("wlan0")) continue;
                Enumeration address = inet.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) address.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        String sAddr = inetAddress.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4) return sAddr;
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "NO IPv4 ADDRESS";
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mIpAddressButton.setText(getWlanIpAddress());
        mVisible = true;
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        mContentView.invalidate();
        return true;
    }

    private OneTimeWorkRequest mWorkRequest = null;
    private boolean mWorkerStatus = true;
    private ImageView mImage = null;
    private HorizontalScrollView mHorizonalScrollView = null;
    private Runnable mTimerUpdateRunable = new Runnable() {

        public void run() {
            long current = System.currentTimeMillis();
            mTimerText.setText(String.format("%.3f\n%.3f", current / 1000.0, current / 1000.0));
            mTimerHandler.postDelayed(this, 0);
            int cur_x = mHorizonalScrollView.getScrollX();
            int max_x = mHorizonalScrollView.getChildAt(0).getMeasuredWidth() - mHorizonalScrollView.getMeasuredWidth();
            if (cur_x >= max_x) mHorizonalScrollView.fullScroll(FOCUS_LEFT);
            mHorizonalScrollView.smoothScrollBy(20, 0);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mHorizonalScrollView = findViewById(R.id.horizontal_view);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mTimerText = findViewById(R.id.text_timer);
        mTimerText.setOnTouchListener(this);

        mWorkRequest = new OneTimeWorkRequest.Builder(TimeSyncWorker.class).build();
        WorkManager.getInstance().enqueue(mWorkRequest);

        mIpAddressButton = findViewById(R.id.btn_ip4_address);
        mIpAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkerStatus = TimeSyncWorker.closeServer();
                if (mWorkerStatus) {
                    Toast.makeText(getApplicationContext(), "TimeSyncService restarted!", Toast.LENGTH_SHORT).show();
                    mWorkRequest = new OneTimeWorkRequest.Builder(TimeSyncWorker.class).build();
                    WorkManager.getInstance().enqueue(mWorkRequest);
                } else {
                    Toast.makeText(getApplicationContext(), "TimeSyncService closed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.
        mImage = findViewById(R.id.img_scroll);
        mImage.setImageResource(R.drawable.background);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mIpAddressButton.setOnTouchListener(mDelayHideTouchListener);

        mTimerHandler.postDelayed(mTimerUpdateRunable, 1);
    }
}
