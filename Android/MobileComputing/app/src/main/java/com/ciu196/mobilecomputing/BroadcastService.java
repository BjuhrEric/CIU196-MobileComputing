package com.ciu196.mobilecomputing;


import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

public class BroadcastService {
    private static String playername = "Ray";

    //Returns if there is currently a session live. Is there somebody currently playing?
    public static boolean isLive(){
        //Todo: not implemented yet, just dummy implementation so far
        return true;


    }

    // Returns the start Instat of the current session. If there isn't one it throws a "NotLiveException"
    public static Duration getCurrentSessionDuration() throws NotLiveException {
        //Todo: not implemented yet, just dummy implementation so far
        if(isLive()){
            return new Duration(Instant.now().minus(1265000), Instant.now());
        }
     throw new NotLiveException();
    }

    //Returns the name of the player currently playing. Returns "No-one" if there is no session
    public static String getPlayerName(){
        //Todo: not implemented yet, just dummy implementation so far

       return (isLive() ? playername : "No-one");

    }
    public void setPlayername(String name){
        playername = name;

    }

    public static boolean closeEnough() {
        //Todo: not implemented yet, just dummy implementation so far
        return true;
    }
}
