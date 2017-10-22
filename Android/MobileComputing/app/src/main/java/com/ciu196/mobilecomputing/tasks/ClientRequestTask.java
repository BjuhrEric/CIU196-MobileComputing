package com.ciu196.mobilecomputing.tasks;

import com.ciu196.mobilecomputing.common.tasks.LoopableTask;
import com.ciu196.mobilecomputing.io.ServerConnection;

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
