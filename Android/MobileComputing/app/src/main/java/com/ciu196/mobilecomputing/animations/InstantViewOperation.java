package com.ciu196.mobilecomputing.animations;

import android.view.View;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Eric on 2017-10-16.
 */

public class InstantViewOperation extends ViewOperation {

    private final List<ViewAction> actions;

    public InstantViewOperation(ViewAction... actions) {
        this(Arrays.asList(actions));
    }

    public InstantViewOperation(List<ViewAction> actions) {
        this.actions = actions;
    }

    public void perform(View view) {
        for (ViewAction action : actions) {
            action.perform();
        }
        for (AnimationEndedListener listener : animationEndedListeners) {
            listener.animationEnded();
        }
    }

}
