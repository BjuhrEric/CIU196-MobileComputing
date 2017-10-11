package com.ciu196.mobilecomputing;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

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

}
