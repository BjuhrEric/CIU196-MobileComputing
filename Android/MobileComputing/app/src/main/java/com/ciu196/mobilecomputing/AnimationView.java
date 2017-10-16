package com.ciu196.mobilecomputing;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Andreas Pegelow on 2017-10-16.
 */
public class AnimationView {

    boolean ongoingAnimation = false;
    Queue<Animation> animationsQueue;
    View v;
    public AnimationView(View v) {
        this.v = v;
        animationsQueue = new ConcurrentLinkedQueue<>();
    }
    public void addAnimation(Animation a){
        animationsQueue.offer(a);

    }
    public void startAnimation(){

        if(!animationsQueue.isEmpty() && !ongoingAnimation){
            Animation a = animationsQueue.poll();
            if (a instanceof AlphaAnimation) {
            }

            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ongoingAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ongoingAnimation = false;
                    startAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(a);
        }
    }
}
