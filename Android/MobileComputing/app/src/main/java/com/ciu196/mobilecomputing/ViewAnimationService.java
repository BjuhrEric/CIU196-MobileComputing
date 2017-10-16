package com.ciu196.mobilecomputing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Andreas Pegelow on 2017-10-11.
 */

public class ViewAnimationService {
    public enum Axis {X, Y};

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
    public static void customFadeAnimation(final View v, int duration, float startAlpha, float endAlpha) {
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

    public static void translateAnimation(final View v, int duration, final Axis axis, float distance) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, distance);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (axis == Axis.X)
                    v.setTranslationX(value);
                else if (axis == Axis.Y)
                    v.setTranslationY(value);


            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.start();


    }
    public static void translateToCenterInParentViewAnimation(final View v, int duration, final Axis axis) {

       View parentView = v.getRootView();

        int vPosition[] = {0,0};
        v.getLocationOnScreen(vPosition);

        int parentPosition[] = {0,0};
        parentView.getLocationOnScreen(parentPosition);
        int goalPosition = 0 ;
        int distance = 0;

        if(axis == Axis.X) {
            goalPosition = (parentView.getWidth() / 2) + parentPosition[0] - (v.getWidth() / 2);
            distance = goalPosition - vPosition[0];
        }else{
            goalPosition = (parentView.getHeight() / 2) + parentPosition[1] - (v.getHeight() / 2);
            distance = goalPosition - vPosition[1];

        }


        ValueAnimator animator = ValueAnimator.ofFloat(0, distance);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (axis == Axis.X)
                    v.setTranslationX(value);
                else if (axis == Axis.Y)
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
    public static void colorTransitionAnimation(final View v, int duration, int colorFrom, int colorTo) {
        ValueAnimator circle1ColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        circle1ColorAnimation.setDuration(duration);
        circle1ColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (v instanceof Circle)
                    ((Circle) v).setColor((int) animator.getAnimatedValue());
                else
                    v.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        circle1ColorAnimation.start();

    }
    public static void colorTransitionAnimationReset(final View v, int duration, int colorFrom, int colorTo) {
        final ValueAnimator circle1ColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        circle1ColorAnimation.setDuration(duration);
        circle1ColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (v instanceof Circle)
                    ((Circle) v).setColor((int) animator.getAnimatedValue());
                else
                    v.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        circle1ColorAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeListener(this);
                animation.setDuration(0);
                ((ValueAnimator) animation).reverse();
            }
        });
        circle1ColorAnimation.start();

    }
}

