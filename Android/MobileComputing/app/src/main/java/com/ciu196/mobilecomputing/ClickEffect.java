package com.ciu196.mobilecomputing;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by PC on 2017-10-09.
 */

public class ClickEffect {

    //click effect
    public static void clickTintEffect(final View v) {
        //in order to handle the click properly.
        //there will be a lag in herobodypress if this is not included
        v.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {}
                }
        );
        v.setOnTouchListener(new

                                     OnSwipeTouchListener(v.getContext()) {

                                         public void onClick() {
                                         }

                                         @Override
                                         public void onActionDown() {
                                             setButtonClickedEffect(v);
                                         }

                                         @Override
                                         public void onActionUp() {
                                             setButtonReleasedEffect(v);
                                         }

                                         @Override
                                         public void onCancel() {
                                             onActionUp();
                                         }

                                     });

    }
    public static void setButtonClickedEffect(View v) {
        //https://stackoverflow.com/questions/8034494/tint-dim-drawable-on-touch

//        Drawable background = v.getBackground();
//        background.setColorFilter(0x99000000,
//                android.graphics.PorterDuff.Mode.DARKEN);
//        v.setBackgroundDrawable(background);
        v.setAlpha(0.3f);
    }
    public static void setButtonReleasedEffect(View v) {
//        Drawable background = v.getBackground();
//        background.setColorFilter(null);
//        v.setBackgroundDrawable(background);
        v.setAlpha(1);
    }

//    final TextView tvDebug = (TextView) findViewById(R.id.debugTxt);
//    Circle outerCircle = (Circle) findViewById(R.id.outerCircle);
//        outerCircle.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(final View view) {
//            final int outerRadius = ((Circle) findViewById(R.id.outerCircle)).getRadius();
//            final int outerColor = ((Circle) findViewById(R.id.outerCircle)).getColor();
//            final int dur = 5 + 1;
//            new CountDownTimer(dur * 1000, 100) {
//                int delta = 1;
//                int tick = 1;
//                int ratio = 10;
//                int secondsLeft = ratio * dur - 1;
//
//                public void onTick(long millisUntilFinished) {
//                    if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {
//                        System.out.println("math: " + Math.round(millisUntilFinished / 500));
//                        System.out.println("secondsleft: " + secondsLeft);
//                        ((Circle) view).setRadius(outerRadius + delta * tick);
//                        ((Circle) view).setColor("#d1172e");
//                        tvDebug.setText(String.valueOf(secondsLeft));
//                        tick++;
//                        secondsLeft--;
//                    }
//                }
//
//                public void onFinish() {
//                    ((Circle) view).setRadius(outerRadius);
//                    ((Circle) view).setColor(outerColor);
//                }
//
//            }.start();
//        }
//    });

}
