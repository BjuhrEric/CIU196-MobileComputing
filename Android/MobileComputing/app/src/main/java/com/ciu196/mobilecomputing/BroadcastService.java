package com.ciu196.mobilecomputing;


import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.json.JSONObject;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.*;

import java.util.SimpleTimeZone;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

public class BroadcastService {
    public static final String TAG = "BroadcastService";
    private static String playername = "Ray";
    String url = "http://appiano-server.herokuapp.com/api/broadcast";
    JSONObject broadcastState;
    RequestQueue queue;
    private static BroadcastService instance;

    private BroadcastService(Context context){
        queue = Volley.newRequestQueue(context);
        getBroadcastState();
    }

    public static BroadcastService getInstance(Context context){
        if(instance == null){
            instance = new BroadcastService(context);
        }
        return instance;
    }

    public void updateBroadcastState(JSONObject newState){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, newState, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "POST onResponse ");
                        Log.i(TAG, response.toString());
                        broadcastState = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());

                    }
                });
        queue.add(jsObjRequest);
        Log.i(TAG, "queueing request");

    }

    public void getBroadcastState(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        broadcastState = response;
                        JSONObject newState = broadcastState;
                        try {
                            newState.put("volume", 1);
                            updateBroadcastState(newState);
                        } catch (Exception e){
                            Log.i(TAG, "JSON put fail....");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, error.toString());

                    }
                });
        queue.add(jsObjRequest);
        Log.i(TAG, "queueing request");
    }

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
