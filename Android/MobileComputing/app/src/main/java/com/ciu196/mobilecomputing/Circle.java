package com.ciu196.mobilecomputing;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class Circle extends View {

    private Point centerPoint;
    int centerX;
    int centerY;
    Paint paint;

    int color;
    int radius;

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
            if(centerPoint == null)
            {
                centerPoint = new Point (getWidth() / 2, getHeight() / 2);
                centerX = centerPoint.x;
                centerY =centerPoint.y;
                radius = getWidth() / 2;
            }
            canvas.drawCircle(centerX, centerY, radius, paint);
//            System.out.println("onDraw: " + radius);
        }
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

    public void setColor(String colorString) {
        paint.setColor(Color.parseColor(colorString));
        invalidate();
    }

    public void setColor(int colorInt) {
        paint.setColor(colorInt);
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public int getColor(){
        return this.color;
    }

    public int getRadius() {
        return this.radius;
    }




}