package com.rlcc.videolibrary.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.OrientationEventListener;

import com.rlcc.videolibrary.widget.VideoPlayerView;

public class ActivityRotationController extends OrientationEventListener {
    private int systemRotation;
    private boolean activityRotation;
    private int activityOrientation;
    private Activity activity;
    private RotationObserver mRotationObserver;
    private VideoPlayerView videoPlayerView;

    public ActivityRotationController(Activity activity, VideoPlayerView playerView) {
        super(activity);
        this.activity = activity;
        this.videoPlayerView = playerView;
        activityOrientation = activity.getResources().getConfiguration().orientation;
        try {
            systemRotation = getScreenRotation(activity.getContentResolver());
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            systemRotation = -1;
        }

        int screenchange = 1;
        try {
            screenchange = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        //screenchange 0 方向锁定， 1 方向可变
        activityRotation = screenchange == 0 ? false : true;
        //创建观察类对象
        mRotationObserver = new RotationObserver(new Handler());
        mRotationObserver.startObserver();
//        openActivityRotation();
        enable();
    }

    /**
     * 打开Activity旋转。
     * 如果打开了屏幕旋转，Activity将接收屏幕旋转事件并执行onConfigurationChanged方法。
     */
    public void openActivityRotation() {
        activityRotation = true;
    }

    /**
     * 关闭Activity旋转。
     * 无论是否打开屏幕旋转，Activity都不能接收到屏幕旋转事件。
     */
    public void closeActivityRotation() {
        activityRotation = false;
    }

    /**
     * 检查Activity能否旋转
     */
    public boolean isActivityRotationEnabled() {
        return activityRotation;
    }

    /**
     * 获取Activity当前方向。
     * 注意，Activity方向不是屏幕方向。只有打开Activity旋转，Activity方向才和屏幕方向保持一致。
     */
    public int getActivityOrientation() {
        return activityOrientation;
    }

    /**
     * 打开对屏幕旋转的监听，并设置屏幕为可旋转。
     */
    @Override
    public void enable() {
        super.enable();
        setScreenRotation(activity.getContentResolver(), 0);
    }

    /**
     * 关闭对屏幕旋转的监听，并恢复到系统之前旋转设定。
     */
    @Override
    public void disable() {
        super.disable();
        if (systemRotation == -1) {
            return;
        }
        mRotationObserver.stopObserver();
        setScreenRotation(activity.getContentResolver(), systemRotation);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation < 0) {
            return;
        }

        int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (orientation >= 0 && orientation <= 60) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if (orientation > 60 && orientation < 120) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if (orientation >= 120 && orientation <= 240) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if (orientation > 240 && orientation < 300) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (orientation >= 300 && orientation <= 360) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            return;
        }
        //是否上锁
        boolean isLock = videoPlayerView.getLockControlView().isLock();
        if ((newOrientation != orientation) && activityRotation && !isLock) {
            activity.setRequestedOrientation(newOrientation);
            activityOrientation = newOrientation;
        }
    }

    private void setScreenRotation(ContentResolver cr, int rotation) {
//        Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION,
//                rotation);
    }

    private int getScreenRotation(ContentResolver cr)
            throws Settings.SettingNotFoundException {
        return Settings.System.getInt(cr,
                Settings.System.ACCELEROMETER_ROTATION);
    }

    private void setScreenOrientation() {
        try {
            int screenchange = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
            //是否开启自动旋转设置 1 开启 0 关闭
            if (screenchange == 1) {
                openActivityRotation();
//                setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                closeActivityRotation();
//                setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    //观察屏幕旋转设置变化，类似于注册动态广播监听变化机制
    private class RotationObserver extends ContentObserver {
        ContentResolver mResolver;

        public RotationObserver(Handler handler) {
            super(handler);
            mResolver = activity.getContentResolver();
            // TODO Auto-generated constructor stub
        }

        //屏幕旋转设置改变时调用
        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            //更新按钮状态
            setScreenOrientation();
        }

        public void startObserver() {
            mResolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        public void stopObserver() {
            mResolver.unregisterContentObserver(this);
        }
    }


}