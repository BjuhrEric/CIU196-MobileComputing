package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerRequestType;
import com.ciu196.mobilecomputing.common.logging.GlobalLog;
import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;

public class ClientConnectionCheckerTask extends ServerTask {

    private final Client client;
    private boolean forcedStop = false;

    public ClientConnectionCheckerTask(final Client client, final Server server) {
        super(server, 1000);
        this.client = client;
    }

    @Override
    protected boolean init() {
        GlobalLog.log("Began checking connection for: "+client.getID());
        return true;
    }

    @Override
    protected boolean finish() {
        try {
            GlobalLog.log("Stopped checking connection for: "+client.getID());
            if (!forcedStop)
                server.detachClient(client, false);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean loop() {
        if (!client.isConnected())
            return false;
        client.addRequest(new ServerRequest(ServerRequestType.CONFIRM_CONNECTIVITY));
        return true;
    }

    @Override
    public void stop() {
        super.stop();
        forcedStop = true;
    }
}
