package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;


public class ClientRequestHandlerTask extends ServerTask {

    private final Client client;

    public ClientRequestHandlerTask(final Client client, final Server server) {
        super(server);
        this.client = client;
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    protected boolean finish() {
        System.out.println("Finishing request handler task for client: "+client.getInetAddress().getHostAddress());
        return true;
    }

    @Override
    protected boolean loop() {
        System.out.println("Handling requests");
        final ClientRequest first = client.getFirstRequest();
        if (first == null)
            return true;
        try {
            switch (first) {
                case BROADCAST:
                    server.setBroadcaster(client);
                    break;
                case DETACH_CLIENT:
                    server.detachClient(client);
                    return false;
                case REQUEST_DATA:
                    server.sendData(client);
                    break;
                case SEND_DATA:
                    server.receiveData(client);
                    break;
                case REQUEST_STATUS:
                    server.sendStatus(client);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
