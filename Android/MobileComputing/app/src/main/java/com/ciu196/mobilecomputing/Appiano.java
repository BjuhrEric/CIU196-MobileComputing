package com.ciu196.mobilecomputing;

import android.app.Application;

import com.ciu196.mobilecomputing.tasks.ClientRequestTask;
import com.ciu196.mobilecomputing.tasks.FetchRequestTask;
import com.ciu196.mobilecomputing.tasks.ServerStatusFetcherTask;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Eric on 2017-10-18.
 */

public class Appiano extends Application {

    private final ServerStatusFetcherTask task;

    public Appiano() {
        task = new ServerStatusFetcherTask(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        new Thread(
                ()-> {
                    new Thread(task).start();
                    new Thread(FetchRequestTask.getInstance()).start();
                    new Thread(ClientRequestTask.getInstance()).start();
                }).start();
    }

}
