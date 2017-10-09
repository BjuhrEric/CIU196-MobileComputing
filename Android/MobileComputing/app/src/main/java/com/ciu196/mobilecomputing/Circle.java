package com.ciu196.mobilecomputing;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class Circle extends View {

    Paint p;
    int color ;
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
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        init();
    }

    public void init()
    {
        p = new Paint();
        p.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(canvas!=null)
        {
            canvas.drawCircle(getHeight()/2, getWidth()/2,getWidth()/2,p );
        }
    }
    public void setColor(int c){
        p.setColor(c);
       invalidate();
    }


}