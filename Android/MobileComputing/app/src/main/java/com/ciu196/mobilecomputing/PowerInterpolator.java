package com.ciu196.mobilecomputing;

import android.view.animation.Interpolator;

/**
 * Created by alexanderalvmo on 2017-10-16.
 */

public class PowerInterpolator implements Interpolator {
    float power;

    public PowerInterpolator(float power){
        this.power = power;
    }

    @Override
    public float getInterpolation(float v) {
        return (float) Math.pow(v, power);
    }
}
