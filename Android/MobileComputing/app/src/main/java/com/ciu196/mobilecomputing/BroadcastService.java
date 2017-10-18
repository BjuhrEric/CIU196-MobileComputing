package com.ciu196.mobilecomputing;


import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.*;

import java.util.SimpleTimeZone;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

public class BroadcastService {
    private static String playername = "Ray";

    //Returns if there is currently a session live. Is there somebody currently playing?
    public static boolean isLive(){
        //Todo: not implemented yet, just dummy implementation so far
        return false;


    }

    // Returns the start Instat of the current session. If there isn't one it throws a "NotLiveException"
    public static Duration getCurrentSessionDuration() throws NotLiveException {
        //Todo: not implemented yet, just dummy implementation so far
        if(isLive()){
            return new Duration(new DateTime(2017,10,16,11,0,0, DateTimeZone.forOffsetHours(2)).toInstant(), Instant.now());

        }
     throw new NotLiveException();
    }

    //Returns the name of the player currently playing. Returns "No-one" if there is no session
    public static String getPlayerName(){
        //Todo: not implemented yet, just dummy implementation so far

       return (isLive() ? playername : "No-one");

    }

    public static void setPlayername(String name){
        playername = name;

    }


    //Returns how many is currently listening
    public static int getNumberOfListeners(){
        //Todo: not implemented yet, just dummy implementation so far
        return 15;
    }


    /*
   * http://googlemaps.github.io/android-maps-utils/javadoc/
   * */

    public static boolean closeEnough() {
        //Todo: not implemented yet, just dummy implementation so far
        int playZone = 0;
        int circleRadius = 0;
        LatLng libLatLng = null;
        LatLng userLatLng = null;

//        double distance = SphericalUtil.computeDistanceBetween(null, null);
        double distance = -1;
        return distance < playZone*circleRadius;

    }
    //Start new brodcast
    public static boolean startNewBroadcast(String nameOfBroadcaster) {
        //Todo: not implemented yet

        if(nameOfBroadcaster.length()>0)
            setPlayername(nameOfBroadcaster);
        else
            setPlayername("Anonymous");

        if(!isLive())
            return true;
        else
            return false;
    }
}
