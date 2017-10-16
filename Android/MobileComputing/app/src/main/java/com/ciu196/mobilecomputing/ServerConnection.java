package com.ciu196.mobilecomputing;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static com.ciu196.mobilecomputing.common.Constants.DATA_PORT;
import static com.ciu196.mobilecomputing.common.Constants.REQUEST_PORT;

public class ServerConnection {

    private final static ServerConnection instance = new ServerConnection();
    private static boolean singletonInstantiated = false;

    private Socket data_socket;
    private Socket request_socket;
    private BufferedInputStream bufferedInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private ServerConnection() {
        if (singletonInstantiated) throw new UnsupportedOperationException("Use singleton");
        singletonInstantiated = true;

        try {
            data_socket = new Socket(InetAddress.getLocalHost(), DATA_PORT);
            request_socket = new Socket(InetAddress.getLocalHost(), REQUEST_PORT);
            bufferedInputStream = new BufferedInputStream(request_socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(request_socket.getOutputStream());
            objectInputStream = new ObjectInputStream(bufferedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerConnection getInstance() {
        return instance;
    }

    public boolean startBroadcast() throws IOException, InterruptedException, ClassNotFoundException {
        ServerResponse serverMessage = sendRequest(ClientRequestType.BROADCAST, "Ray Charles");
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public boolean detach() throws IOException, InterruptedException, ClassNotFoundException {
        ServerResponse serverMessage = sendRequest(ClientRequestType.DETACH_CLIENT);
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public ServerResponse.Status getStatus()  throws IOException, InterruptedException, ClassNotFoundException {
        ServerResponse serverResponse = sendRequest(ClientRequestType.REQUEST_STATUS);
        return (ServerResponse.Status) serverResponse.getValue();
    }

    private ServerResponse sendRequest(final ClientRequestType request) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
        return sendRequest(new ClientRequest(request));
    }

    private ServerResponse sendRequest(final ClientRequestType request, final String val) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
        return sendRequest(new ClientRequest(request, val));
    }

    private ServerResponse sendRequest(final ClientRequest request) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
        Object o = null;

        objectOutputStream.writeObject(request);
        objectOutputStream.flush();


        while (bufferedInputStream.available() <= 0 && request_socket.isConnected())
            Thread.sleep(10);


        if (request_socket.isConnected())
            o = objectInputStream.readObject();

        return (ServerResponse) o;
    }

}
