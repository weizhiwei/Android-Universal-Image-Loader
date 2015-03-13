package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by zhiweiwei on 3/13/15.
 */
public class InterceptableFrameLayout extends FrameLayout {
    private OnInterceptTouchListener onInterceptTouchListener;

    public InterceptableFrameLayout(Context context) {
        super(context);
    }

    public InterceptableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener l) {
        onInterceptTouchListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (null != onInterceptTouchListener) {
            return onInterceptTouchListener.onInterceptTouch(this, event);
        }
        return false;
    }
}
