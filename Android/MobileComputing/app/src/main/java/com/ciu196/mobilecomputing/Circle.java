package com.ciu196.mobilecomputing;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;


public class Circle extends View {

    int centerX;
    int centerY;
    Paint paint;
    int h;
    int w;

    int color;
    int radius;
    int mRadius;

    public Circle(Context context) {
        this(context, null);
    }

    public Circle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Circle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // real work here
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Circle,
                0, 0
        );

        try {

            color = a.getColor(R.styleable.Circle_circleColor, 0xff000000);
//            radius = a.getInt(R.styleable.Circle_circleRadius, 10);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }

    private TextView txtID;

    public void init() {
        paint = new Paint();
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (canvas != null) {
            if (centerX == 0) {
                updateCenterPoint();
            }
            canvas.drawCircle(centerX, centerY, radius, paint);
//            System.out.println("onDraw: " + radius);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heighMeasureSpec) {
        if (centerX == 0)
            super.onMeasure(widthMeasureSpec, heighMeasureSpec);
        else
            setMeasuredDimension(w, h);
    }

    public void setColor(String colorString) {
        paint.setColor(Color.parseColor(colorString));
        invalidate();
    }

    public void setColor(int colorInt) {
        paint.setColor(colorInt);
        this.color = colorInt;
        invalidate();
    }

    void setRadius(int radius) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = radius * 2;
        params.width = radius * 2;
        this.requestLayout();
        w=params.width;
        h=params.height;

        centerX = radius;
        centerY = radius;
        this.radius = radius;
//        updateCenterPoint(); //radius updated

        invalidate();
    }

    void setRadius2(int radius) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = radius * 2;
        params.width = radius * 2;
        this.requestLayout();
        w=params.width;
        h=params.height;

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha",  1f, .3f);
        fadeOut.setDuration(300);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(this, "alpha", .3f, 1f);
        fadeIn.setDuration(300);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                        mAnimationSet.start();
            }
        });
        mAnimationSet.start();

        centerX = radius;
        centerY = radius;
        this.radius = radius;
//        updateCenterPoint(); //radius updated

        invalidate();
    }

    public void resetRadius() {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = mRadius * 2;
        params.width = mRadius * 2;
        this.requestLayout();
        w=params.width;
        h=params.height;

        centerX = mRadius;
        centerY = mRadius;
        this.radius = mRadius;

        invalidate();
    }

    private void updateCenterPoint() {
        this.centerX = getWidth() / 2;
        this.centerY = getHeight() / 2;
        this.radius = getWidth() / 2;
        if (this.mRadius == 0)
            mRadius = getWidth() / 2;
    }

    public int getColor() {
        return this.color;
    }

    public int getRadius() {
        return this.radius;
    }

    public boolean insideCircle(MotionEvent event) {
        //CIRCLE :      (x-a)^2 + (y-b)^2 = r^2
        float touchX, touchY;
        touchX = event.getX();
        touchY = event.getY();
//        System.out.println("centerX = "+this.centerX+", centerY = "+this.centerY);
//        System.out.println("touchX = "+touchX+", touchY = "+touchY);
//        System.out.println("radius = "+this.radius);
        if (Math.pow(touchX - this.centerX, 2) + Math.pow(touchY - this.centerY, 2) < Math.pow(this.radius, 2)) {
            System.out.println("Inside Circle");
            return true;
        } else {
            System.out.println("Outside Circle");
            return false;
        }
    }


}