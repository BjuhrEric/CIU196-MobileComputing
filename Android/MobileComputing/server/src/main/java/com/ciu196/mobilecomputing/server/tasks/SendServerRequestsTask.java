package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.requests.ClientResponse;
import com.ciu196.mobilecomputing.common.requests.ClientResponseType;
import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Eric on 2017-10-18.
 */

public class SendServerRequestsTask extends ServerTask {

    public SendServerRequestsTask(Server server) {
        super(server, 5);
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
        Collection<Client> clients = server.getClients();
        clients.forEach(Client::sendRequest);
        return true;
    }
}
