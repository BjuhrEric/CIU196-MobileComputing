package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.Reaction;
import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.logging.GlobalLog;
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
        GlobalLog.log("Finishing request handler task for client: " + client.getInetAddress().getHostAddress());
        return true;
    }

    @Override
    protected boolean loop() {
        if (!client.isConnected())
            return false;
        final ClientRequest first = client.getFirstRequest();
        if (first == null)
            return true;
        try {
            switch (first.getType()) {
                case BROADCAST:
                    server.setBroadcaster(client, first.getValue());
                    break;
                case STOP_BROADCAST:
                    server.stopBroadcast(client);
                    break;
                case LISTEN:
                    server.addListener(client);
                    break;
                case STOP_LISTEN:
                    server.removeListener(client);
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
                case SEND_REACTION:
                    server.shareReaction(client, Reaction.valueOf(first.getValue()));
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            GlobalLog.log(e);
            return false;
        }
        return true;
    }
}
