package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.logging.GlobalLog;
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
            server.connectServerRequestSocket();
            return true;
        } catch (Exception e) {
            GlobalLog.log(e);
            return false;
        }
    }
}
