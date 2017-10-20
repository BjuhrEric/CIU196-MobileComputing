package com.ciu196.mobilecomputing.server.util;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientResponse;
import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerRequestType;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;

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
    void sendResponse(ServerResponse response) throws IOException;
    void addRequest(ServerRequest request);
    ClientResponse sendRequest();
    InetAddress getInetAddress();
    boolean isConnected();
}
