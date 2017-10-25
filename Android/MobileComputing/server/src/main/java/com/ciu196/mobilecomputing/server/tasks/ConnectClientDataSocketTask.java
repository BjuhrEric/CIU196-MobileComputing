package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.logging.GlobalLog;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;

public class ConnectClientDataSocketTask extends ServerTask {

    public ConnectClientDataSocketTask(Server server) {
        super(server, 0);
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    protected boolean finish() {
        server.quit();
        return true;
    }

    @Override
    protected boolean loop() {
        try {
            server.connectDataSocket();
            return true;
        } catch (IOException e) {
            GlobalLog.log(e);
            return false;
        }
    }

}
