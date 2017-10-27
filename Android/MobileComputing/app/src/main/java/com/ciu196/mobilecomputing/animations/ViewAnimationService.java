package com.ciu196.mobilecomputing.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.ciu196.mobilecomputing.Circle;
import com.ciu196.mobilecomputing.ConnectActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andreas Pegelow on 2017-10-11.
 */

public class ViewAnimationService {
    public enum Axis {X, Y}

    ;
    static Map<View, AnimationView> animationViewMap = new HashMap<>();
    public static final String TAG = "ViewAnimationService";

    public static void startAllAnimations() {

        for (AnimationView a : animationViewMap.values()) {
            a.startAnimation();
        }
    }

    public static void startAnimations(View view) {
        animationViewMap.get(view).startAnimation();
    }

    public static void addInstantOperation(final View v, ViewAction... actions) {
        AnimationView animationView = animationViewMap.get(v);

        if (animationView == null) {
            animationView = new AnimationView(v);
            animationViewMap.put(v, animationView);
        }

        animationView.addAnimation(new InstantViewOperation(actions));
    }

    public static void addAnimator(final View v, final Animator a) {
        AnimationView animationView = animationViewMap.get(v);

        if (animationView == null) {
            animationView = new AnimationView(v);
            animationViewMap.put(v, animationView);
        }
        animationView.addAnimation(new AnimatorOperation(a));
    }

    public static void addFadeOutAnimation(final View v, int duration) {
        addAnimator(v, getFadeAnimator(v, duration, 1, 0));
    }

    public static void addFadeInAnimation(final View v, int duration) {
        addAnimator(v, getFadeAnimator(v, duration, 0, 1));
    }

    public static Animator getFadeAnimator(final View v, int duration, float startAlpha, float endAlpha) {
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

        return animator;
    }

    public static void startFadeAnimator(final View v, int duration, float startAlpha, float endAlpha) {
        getFadeAnimator(v, duration, startAlpha, endAlpha).start();
    }

    public static Animator getTranslationAnimator(final View v, int duration, final Axis axis, float distance) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, distance);
        final float startX = v.getX(), startY = v.getY();

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                if (axis == Axis.X)
                    //v.setTranslationX(value);
                    v.setX(startX + value);

                else if (axis == Axis.Y)
                    //v.setTranslationY(value);
                    v.setY(startY + value);

            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);

        return animator;
    }

    public static void startTranslationAnimator(final View v, int duration, final Axis axis, float distance) {
        getTranslationAnimator(v, duration, axis, distance).start();
    }

    public static Animator getTranslateToCenterInParentViewAnimator2(final View v, int duration, final Axis axis) {

        View parentView = v.getRootView();

        int vPosition[] = {0, 0};
        v.getLocationOnScreen(vPosition);

        int parentPosition[] = {0, 0};
        parentView.getLocationOnScreen(parentPosition);
        int goalPosition = 0;
        int distance = 0;

        if (axis == Axis.X) {
            goalPosition = (parentView.getWidth() / 2) + parentPosition[0] - (v.getWidth() / 2);
            distance = goalPosition - vPosition[0];
        } else {
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

        return animator;
    }

    public static Animator getTranslationAnimatorReset(final View v, int duration, final Axis axis, final String specialCase) {
        ObjectAnimator animator = null;
        String propertyName;

        if (axis == Axis.X) {
            propertyName = "translationX";
            if (specialCase.equals("pianoStatusTextView")) {
                animator = ObjectAnimator.ofFloat(v, propertyName, -10);
            } else if (specialCase.equals("playerNameTextView")) {
                animator = ObjectAnimator.ofFloat(v, propertyName, v.getRight(), -10);
            }
        } else {
            propertyName = "translationY";
            if (specialCase.equals("playerNameTextView")) {
                animator = ObjectAnimator.ofFloat(v, propertyName, v.getTop(), 0);
            } else if (specialCase.equals("listenerLayout")) {
                animator = ObjectAnimator.ofFloat(v, propertyName, -10);
            }
        }

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);

        return animator;
    }

    public static Animator getTranslateToCenterInParentViewAnimator(final View v, int duration, final Axis axis, final String specialCase) {
        int target;
        if (axis == Axis.X) {
            if(specialCase.equals("pianoStatusTextView"))
                target = -150;
            else {
                target = 350;
                //int screenWidth = ConnectActivity.SCREEN_WIDTH;
                //target = (screenWidth / 2) - (v.getLeft()) - v.getWidth();
                //System.out.println(specialCase + ": screen witdh: " + screenWidth + "getwidth: " + v.getWidth() + " target: " + target);
                //Snackbar.make(v, specialCase + ":screen witdh: " + screenWidth + " target: " + target, Snackbar.LENGTH_INDEFINITE);
            }
        } else {
            target = 90;
        }


        ValueAnimator animator = ValueAnimator.ofFloat(0, target);
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

        return animator;
    }

    public static Animator getUniformScaleAnimator(final View v, int duration, float scaleFactor) {

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
        return animator;
    }

    public static Animator getUniformScaleAnimatorReset(final View v, int duration, float scaleFactor) {

        ValueAnimator animator = ValueAnimator.ofFloat(scaleFactor, 1);
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
        return animator;
    }

    public static void startUniformScaleAnimator(final View v, int duration, float scaleFactor) {
        getUniformScaleAnimator(v, duration, scaleFactor).start();
    }

    public static Animator getColorTransitionAnimator(final View v, int duration, int colorFrom, int colorTo) {
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
        return animation;
    }

    public static void startColorTransitionAnimator(final View v, int duration, int colorFrom, int colorTo) {
        getColorTransitionAnimator(v, duration, colorFrom, colorTo).start();
    }

    public static Animator getColorTransitionAndBackAnimator(final View v, final int duration, final int colorFrom, final int colorTo) {
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
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startColorTransitionAnimator(v, duration, colorTo, colorFrom);
            }

        });
        animation.start();
        return animation;
    }

    public static void startColorTransitionAndBackAnimator(final View v, final int duration, final int colorFrom, final int colorTo) {
        getColorTransitionAndBackAnimator(v, duration, colorFrom, colorTo).start();
    }

    public static Animator getElevationTransitionAnimator(final View view, int duration, float elevation) {
        ValueAnimator animation = ValueAnimator.ofFloat(0, elevation);
        animation.setDuration(duration);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
            }
        });
        return animation;
    }

    public static Animator getFadeWithScaleAnimator(View view, int duration, float scale, int fadeFrom, int fadeTo) {
        AnimatorSet animation = new AnimatorSet();
        animation
                .play(ViewAnimationService.getFadeAnimator(view, duration, fadeFrom, fadeTo))
                .with(ViewAnimationService.getUniformScaleAnimator(view, duration, scale));
        return animation;
    }


    public static void addFadeWithScaleAnimation(View view, int duration, float scale, int fadeFrom, int fadeTo) {
        addAnimator(view, getFadeWithScaleAnimator(view, duration, scale, fadeFrom, fadeTo));
    }

    public static Animator getWiggleAnimator(View v, int duration, float fromDeg, float toDeg) {
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(v,
                "rotation", fromDeg, toDeg);
        imageViewObjectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        imageViewObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        imageViewObjectAnimator.setInterpolator(new DecelerateInterpolator());
        imageViewObjectAnimator.setDuration(duration);
        return imageViewObjectAnimator;

    }
}
