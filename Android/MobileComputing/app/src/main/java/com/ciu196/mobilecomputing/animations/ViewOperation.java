package com.ciu196.mobilecomputing.animations;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eric on 2017-10-16.
 */

public abstract class ViewOperation {

    protected final List<AnimationEndedListener> animationEndedListeners;

    protected ViewOperation() {
        this.animationEndedListeners = new LinkedList<>();
    }

    public abstract void perform(final View view);

    public void addAnimationEndedListener(final AnimationEndedListener listener) {
        animationEndedListeners.add(listener);
    }

}
