package com.ciu196.mobilecomputing;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Andreas Pegelow on 2017-10-11.
 */

public class ViewAnimationService {

    public static void fadeOutAnimation(final View v, int duration) {
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(duration);
        v.startAnimation(out);
        out.setAnimationListener(new Animation.AnimationListener() {
                                     @Override
                                     public void onAnimationStart(Animation animation) {

                                     }

                                     @Override
                                     public void onAnimationEnd(Animation animation) {
                                         v.setVisibility(View.INVISIBLE);
                                     }

                                     @Override
                                     public void onAnimationRepeat(Animation animation) {

                                     }
                                 }
        );

    }

    public static void fadeInAnimation(final View v, int duration) {
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(duration);
        v.startAnimation(in);
        in.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        v.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                }
        );

    }
    public static void costumFadeAnimation(final View v, int duration, float startAlpha, float endAlpha) {
        ValueAnimator animator = ValueAnimator.ofFloat(startAlpha, endAlpha);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                v.setAlpha(value);
            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();


    }

    public static void translateAnimation(final View v, int duration, final ConnectActivity.Axis axis, float distance) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, distance);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (axis == ConnectActivity.Axis.X)
                    v.setTranslationX(value);
                else if (axis == ConnectActivity.Axis.Y)
                    v.setTranslationY(value);


            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();


    }

    public static void uniformScaleAnimation(final View v, int duration, float scaleFactor) {

        ValueAnimator animator = ValueAnimator.ofFloat(1, scaleFactor);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();


                v.setScaleX(value);
                v.setScaleY(value);

            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();


    }
}
