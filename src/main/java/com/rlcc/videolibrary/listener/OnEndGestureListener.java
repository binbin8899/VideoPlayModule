package com.rlcc.videolibrary.listener;

import android.view.MotionEvent;

public interface OnEndGestureListener {
    void onEndGesture();

    boolean onTouchEvent(MotionEvent event);
}
