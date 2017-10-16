package com.ciu196.mobilecomputing;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

public class OnlineBroadcastService {
    private final static String NOONE = "No-one";
    private final static OnlineBroadcastService instance = new OnlineBroadcastService();
    private static boolean  singletonInstantiated = false;

    private volatile String broadcasterName = NOONE;
    private volatile boolean isLive;
    private volatile long broadcastStartTime;
    private volatile int numberOfListeners;

    private OnlineBroadcastService() {
        if (singletonInstantiated) throw new UnsupportedOperationException("Use singleton");
        singletonInstantiated = true;
    }

    public static OnlineBroadcastService getInstance() {
        return instance;
    }

    //Returns if there is currently a session live. Is there somebody currently playing?
    public boolean isLive(){
        return isLive;
    }

    public String getBroadcasterName() {
        return isLive ? getBroadcasterName() : NOONE;
    }

    public long getBroadcastStartTime() {
        return broadcastStartTime;
    }

    public int getNumberOfListeners() {
        return numberOfListeners;
    }

    public void setBroadcasterName(String broadcasterName) {
        this.broadcasterName = broadcasterName;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public void setBroadcastStartTime(long broadcastStartTime) {
        this.broadcastStartTime = broadcastStartTime;
    }

    public void setNumberOfListeners(int numberOfListeners) {
        this.numberOfListeners = numberOfListeners;
    }


    // Returns the start Instat of the current session. If there isn't one it throws a "NotLiveException"
    public Duration getCurrentSessionDuration() throws NotLiveException {
        //Todo: not implemented yet, just dummy implementation so far
        if (isLive()) {
            return new Duration(new DateTime(broadcastStartTime).toInstant(), Instant.now());

        }
        throw new NotLiveException();
    }

}
