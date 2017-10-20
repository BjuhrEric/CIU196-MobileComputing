package com.ciu196.mobilecomputing;

import android.support.annotation.NonNull;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Eric on 2017-10-18.
 */

public final class ClientRequestTask extends LoopableTask {

    private final static ClientRequestTask instance = new ClientRequestTask();

    private ClientRequestTask() {
        super(50);
    }

    public static ClientRequestTask getInstance() {
        return instance;
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    protected boolean finish() {
        return true;
    }

    @Override
    protected boolean loop() {
        ServerConnection.getInstance().sendRequest();
        return true;
    }
}
