package com.ciu196.mobilecomputing;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


public class Circle extends View {

    Paint p;
    int color ;
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
            radius = a.getInt(R.styleable.Circle_circleRadius, 10);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }

    private TextView txtID;

    public void init()
    {
        txtID = (TextView) findViewById(R.id.textID);
        p = new Paint();
        p.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(canvas!=null)
        {

            int w = getWidth();
            int h = getHeight();
//
//            int pl = getPaddingLeft();
//            int pr = getPaddingRight();
//            int pt = getPaddingTop();
//            int pb = getPaddingBottom();
//
//            int usableWidth = w - (pl + pr);
//            int usableHeight = h - (pt + pb);
//
//            int radius = Math.min(usableWidth, usableHeight) / 2;
//            int cx = pl + (usableWidth / 2);
//            int cy = pt + (usableHeight / 2);

//            canvas.drawCircle(cx, cy, radius, p);

            canvas.drawCircle(getWidth()/2, getHeight()/2,radius,p );
        }
    }
    public void setColor(String colorString){
       p.setColor(Color.parseColor(colorString));
       invalidate();
    }


}