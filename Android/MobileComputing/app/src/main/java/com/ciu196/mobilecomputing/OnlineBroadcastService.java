package com.ciu196.mobilecomputing;

import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    private volatile int volume;
    private ReactionListener reactionListener;


    private final List<StatusUpdateListener> updateListeners;

    private OnlineBroadcastService() {
        if (singletonInstantiated) throw new UnsupportedOperationException("Use singleton");
        singletonInstantiated = true;
        this.updateListeners = new LinkedList<>();
    }


    public static OnlineBroadcastService getInstance() {
        return instance;
    }

    public void addStatusUpdateListener(final StatusUpdateListener listener) {
        updateListeners.add(listener);
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

    public int getVolume(){ return volume;}

    public void setBroadcasterName(String broadcasterName) {
        this.broadcasterName = broadcasterName;
    }

    public void setLive(boolean live) {
        if (isLive != live)
            if (live)
                for (StatusUpdateListener listener : updateListeners) listener.onBroadcastStarted();
        if (!live)
            for (StatusUpdateListener listener : updateListeners) listener.onBroadcastEnded();
        isLive = live;
    }

    public void setBroadcastStartTime(long broadcastStartTime) {
        this.broadcastStartTime = broadcastStartTime;
    }

    public void setNumberOfListeners(int numberOfListeners) {
        if (numberOfListeners != this.numberOfListeners)
            for (StatusUpdateListener listener : updateListeners) listener.onNumberOfListenersChanged();
        this.numberOfListeners = numberOfListeners;
    }

    public void setVolume(int volume){
        this.volume = volume;
    }

    public void incomingReaction(Reaction reaction){
        reactionListener.onReactionReceived(reaction);
    }

    public void sendReaction(Reaction reaction){
        //// TODO: 2017-10-18

    }

    // Returns the start Instat of the current session. If there isn't one it throws a "NotLiveException"
    public Duration getCurrentSessionDuration() throws NotLiveException {
        //Todo: not implemented yet, just dummy implementation so far
        if (isLive()) {
            return new Duration(new DateTime(broadcastStartTime).toInstant(), Instant.now());

        }
        throw new NotLiveException();
    }

    public void setReactionListener(ReactionListener l){
        reactionListener = l;
    }

}
