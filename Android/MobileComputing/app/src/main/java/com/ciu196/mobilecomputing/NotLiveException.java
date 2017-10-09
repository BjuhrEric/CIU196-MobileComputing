package com.ciu196.mobilecomputing;

/**
 * Created by Andreas Pegelow on 2017-10-09.
 */

public class NotLiveException extends Exception {
    public NotLiveException() {
        super("No session currently live");

    }
    public NotLiveException(String message) {
        super("No session currently live "+message);
    }
}
