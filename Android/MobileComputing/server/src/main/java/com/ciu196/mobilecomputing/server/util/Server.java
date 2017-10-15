package com.ciu196.mobilecomputing.server.util;

import java.io.IOException;

/**
 * Created by Eric on 2017-10-12.
 */

public interface Server {
    void quit();
    void connectRequestSocket() throws IOException;
    void connectDataSocket() throws IOException;
    void setBroadcaster(Client client) throws IOException;
    void detachClient(Client client) throws IOException;
    void sendData(Client client) throws IOException;
    void receiveData(Client client) throws IllegalStateException, IOException;
    void sendStatus(Client client) throws IOException;
    void init() throws IOException;
}
