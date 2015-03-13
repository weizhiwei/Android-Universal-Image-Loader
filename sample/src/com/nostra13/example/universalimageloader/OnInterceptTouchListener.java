package com.nostra13.example.universalimageloader;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhiweiwei on 3/13/15.
 */
public interface OnInterceptTouchListener {
    boolean onInterceptTouch(View v, MotionEvent event);
}
