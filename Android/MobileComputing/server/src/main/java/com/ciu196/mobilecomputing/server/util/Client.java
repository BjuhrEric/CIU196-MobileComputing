package com.ciu196.mobilecomputing.server.util;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerMessage;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Eric on 2017-10-12.
 */

public interface Client extends Comparable<Client> {
    ClientRequest getFirstRequest();
    void fetchRequests() throws IOException, ClassNotFoundException;
    byte[] readData() throws IOException;
    void sendData() throws IOException;
    void addOutputData(byte[] data);
    void close() throws IOException;
    void sendMessage(ServerMessage response) throws IOException;
    InetAddress getInetAddress();
    boolean isConnected();
}
