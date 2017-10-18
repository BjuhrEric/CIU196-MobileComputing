package com.ciu196.mobilecomputing.server.util;

import com.ciu196.mobilecomputing.Reaction;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Eric on 2017-10-12.
 */

public interface Server {
    void quit();
    void connectRequestSocket() throws IOException;
    void connectDataSocket() throws IOException;
    void connectServerRequestSocket() throws IOException;
    void setBroadcaster(Client client, String name) throws IOException;
    void stopBroadcast(Client c) throws IOException;
    void addListener(Client c) throws IOException;
    void removeListener(Client c) throws IOException;
    Collection<Client> getClients();
    void detachClient(Client client) throws IOException;
    void detachClient(Client client, boolean response) throws IOException;
    void sendData(Client client) throws IOException;
    void receiveData(Client client) throws IllegalStateException, IOException;
    void sendStatus(Client client) throws IOException;
    void init() throws IOException;
    void shareReaction(Reaction reaction);
}
