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
public class AnimationView implements AnimationEndedListener {

    boolean ongoingAnimation = false;
    Queue<ViewOperation> animationsQueue;
    View v;

    public AnimationView(View v) {
        this.v = v;
        animationsQueue = new ConcurrentLinkedQueue<>();
    }

    public void addAnimation(ViewOperation a){
        animationsQueue.offer(a);
    }

    public void startAnimation(){
        if(!animationsQueue.isEmpty() && !ongoingAnimation){
            ViewOperation op = animationsQueue.poll();
            op.addAnimationEndedListener(this);
            op.perform(v);
        }
    }

    @Override
    public void animationEnded() {
        ongoingAnimation = false;
        startAnimation();
    }
}
