package com.ciu196.mobilecomputing;

import android.app.Application;

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
        new Thread(
                ()-> {
                    new Thread(task).start();
                    new Thread(FetchRequestTask.getInstance()).start();
                    new Thread(ClientRequestTask.getInstance()).start();
                }).start();
    }

}
