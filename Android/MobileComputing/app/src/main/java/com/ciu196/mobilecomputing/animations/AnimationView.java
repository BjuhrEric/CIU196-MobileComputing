package com.ciu196.mobilecomputing.animations;

import android.view.View;

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
