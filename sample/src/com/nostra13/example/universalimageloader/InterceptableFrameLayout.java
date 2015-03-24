package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setInterceptedParents();
    }

    public void setInterceptedParents() {
        setOnInterceptTouchListener(new OnInterceptTouchListener () {
            @Override
            public boolean onInterceptTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        ViewParent p = v.getParent();
                        while (null != p) {
                            p.requestDisallowInterceptTouchEvent(true);
                            p = p.getParent();
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        p = v.getParent();
                        while (null != p) {
                            p.requestDisallowInterceptTouchEvent(false);
                            p = p.getParent();
                        }
                        break;
                }
                return false;
            }
        });
    }
}
