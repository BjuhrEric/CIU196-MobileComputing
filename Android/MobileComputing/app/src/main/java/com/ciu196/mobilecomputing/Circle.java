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

    Paint paint;
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
        paint = new Paint();
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(canvas!=null)
        {

            int w = getWidth();
            int h = getHeight();

            canvas.drawCircle(getWidth()/2, getHeight()/2,radius, paint);
        }
    }
    public void setColor(String colorString){
       paint.setColor(Color.parseColor(colorString));
       invalidate();
    }

    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
    }


}