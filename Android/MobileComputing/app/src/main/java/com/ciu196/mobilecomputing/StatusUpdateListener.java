package com.ciu196.mobilecomputing;

/**
 * Created by Eric on 2017-10-18.
 */

public interface StatusUpdateListener {

    void onBroadcastStarted();
    void onBroadcastEnded();
    void onNumberOfListenersChanged();

}
