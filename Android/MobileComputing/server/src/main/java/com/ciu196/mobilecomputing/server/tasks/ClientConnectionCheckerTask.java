package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;

public class ClientConnectionCheckerTask extends ServerTask {

    private final Client client;
    private boolean forcedStop = false;

    public ClientConnectionCheckerTask(final Client client, final Server server) {
        super(server, 10000);
        this.client = client;
    }

    @Override
    protected boolean init() {
        return client.isConnected();
    }

    @Override
    protected boolean finish() {
        try {
            if (!forcedStop)
                server.detachClient(client);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean loop() {
        System.out.println("Checking connection for client: "+client.getInetAddress().getHostAddress());
        return client.isConnected();
    }

    @Override
    public void stop() {
        super.stop();
        forcedStop = true;
    }
}
