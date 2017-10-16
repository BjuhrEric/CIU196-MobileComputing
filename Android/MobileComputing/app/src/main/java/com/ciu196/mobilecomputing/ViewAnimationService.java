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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andreas Pegelow on 2017-10-11.
 */

public class ViewAnimationService {
    public enum Axis {X, Y};
    static Map<View,AnimationView> map = new HashMap<>();

    public static void startAllAnimation(){

        for(AnimationView a : map.values()){
            a.startAnimation();

        }
    }

    public static void fadeOutAnimation(final View v, int duration) {
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(duration);

        AnimationView animationView= map.get(v);

       if(animationView == null){
           animationView = new AnimationView(v);
           map.put(v, animationView);
       }
        animationView.addAnimation(out);
        //animationView.startAnimation();

    }

    public static void fadeInAnimation(final View v, int duration) {
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(duration);

        AnimationView animationView= map.get(v);

        if(animationView == null){
            animationView = new AnimationView(v);
            map.put(v, animationView);
        }
        animationView.addAnimation(in);
        //animationView.startAnimation();

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
        ValueAnimator animation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (v instanceof Circle)
                    ((Circle) v).setColor((int) animator.getAnimatedValue());
                else
                    v.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        animation.start();

    }
    public static void colorTransitionAndBackAnimation(final View v, final int duration, final int colorFrom, final int colorTo) {
        ValueAnimator animation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (v instanceof Circle)
                    ((Circle) v).setColor((int) animator.getAnimatedValue());
                else
                    v.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        animation.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation)
            {
                colorTransitionAnimation(v, duration, colorTo, colorFrom);
            }

        });
        animation.start();

    }
}
