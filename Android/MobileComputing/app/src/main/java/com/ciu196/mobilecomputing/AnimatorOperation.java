package com.ciu196.mobilecomputing;

import android.animation.Animator;
import android.view.View;

/**
 * Created by Eric on 2017-10-16.
 */

public class AnimatorOperation extends ViewOperation {

    private final Animator a;

    public AnimatorOperation(final Animator a) {
        this.a = a;
    }


    @Override
    public void perform(final View view) {
        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (AnimationEndedListener listener : animationEndedListeners) {
                    listener.animationEnded();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        a.start();
    }
}