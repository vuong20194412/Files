package com.example.files.nonActivity;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchZoomImageView extends android.view.View {

    private final ScaleGestureDetector mScaleDetector;
    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private float constScaleFactor;
    private float mScaleFactor;

    private Drawable mIcon;
    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;

    public TouchZoomImageView(Context context) {
        this(context, null, 0);
    }

    public TouchZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            /*case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }*/

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    public void setImage(Drawable drawable) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((android.view.WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        int wHeight = Math.round(metrics.scaledDensity * metrics.heightPixels / metrics.density);
        int wWidth = Math.round(metrics.scaledDensity * metrics.widthPixels / metrics.density);
        mIcon = drawable;
        int mWidth = mIcon.getIntrinsicWidth();
        int mHeight = mIcon.getIntrinsicHeight();
        Log.e("TAG", "wW " + wWidth + " wH " + wHeight);
        Log.e("TAG", "mW " + mWidth + " mH " + mHeight);

        constScaleFactor = mScaleFactor = (float) wWidth / (float) mWidth;
        mPosY = (Math.round(wHeight / mScaleFactor) + mHeight) / 2f;
        mIcon.setBounds(0, 0, mWidth, mHeight);
        Log.e("TAG","" + mPosY);
    }

    @Override
    public void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
        if (mIcon != null) mIcon.draw(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(constScaleFactor, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}
