package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.Constants;
import com.ciu196.mobilecomputing.server.util.Server;

/**
 * Created by Eric on 2017-10-18.
 */

public class ConnectClientServerRequestSocketTask extends ServerTask {

    public ConnectClientServerRequestSocketTask(Server server) {
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
            System.out.println("Waiting for clients to connect to port "+ Constants.SERVER_REQUEST_PORT);
            server.connectServerRequestSocket();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
