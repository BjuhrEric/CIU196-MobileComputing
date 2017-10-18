package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.Constants;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;

public class ConnectClientRequestSocketTask extends ServerTask {

    public ConnectClientRequestSocketTask(Server server) {
        super(server, 0);
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
        try {
            System.out.println("Waiting for clients to connect to port "+ Constants.REQUEST_PORT);
            server.connectRequestSocket();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
