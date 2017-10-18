package com.ciu196.mobilecomputing;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
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
            request_socket = new Socket(InetAddress.getByName("46.239.104.32"), REQUEST_PORT);
            bufferedInputStream = new BufferedInputStream(request_socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(request_socket.getOutputStream());
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            while (bufferedInputStream.available() <= 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

            ServerResponse response = (ServerResponse) objectInputStream.readObject(); //Confirmation of connection.
            if (response.getType() == ServerResponse.ResponseType.DETACHED) {
                // Detached from previous session, wait for confirmation for the new session.
                while (bufferedInputStream.available() <= 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                objectInputStream.readObject();
            }

            data_socket = new Socket(InetAddress.getByName("46.239.104.32"), DATA_PORT);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ServerConnection getInstance() {
        return instance;
    }

    public void startBroadcast(final String name) {
        addTaskRequest(ClientRequestType.BROADCAST, name);
    }

    public void stopBroadcast() {
        addTaskRequest(ClientRequestType.STOP_BROADCAST);
    }

    public void startListen() {
        addTaskRequest(ClientRequestType.LISTEN);
    }

    public void stopListen() {
        addTaskRequest(ClientRequestType.STOP_LISTEN);
    }

    public void startBroadcast(final String name, RequestDoneListener... listeners) {
        addTaskRequest(ClientRequestType.BROADCAST, name, listeners);
    }

    public void stopBroadcast(RequestDoneListener... listeners) {
        addTaskRequest(ClientRequestType.STOP_BROADCAST, listeners);
    }

    public void startListen(RequestDoneListener... listeners) {
        addTaskRequest(ClientRequestType.LISTEN, listeners);
    }

    public void stopListen(RequestDoneListener... listeners) {
        addTaskRequest(ClientRequestType.STOP_LISTEN, listeners);
    }

    public void fetchStatus(RequestDoneListener... listeners) {
        addTaskRequest(ClientRequestType.REQUEST_STATUS, listeners);
    }

    public void detach() throws IOException, InterruptedException, ClassNotFoundException {
        addTaskRequest(ClientRequestType.DETACH_CLIENT);
    }

    private void addTaskRequest(final ClientRequestType requestType) {
        addTaskRequest(new ClientRequest(requestType));
    }

    private void addTaskRequest(final ClientRequestType requestType, final String value) {
        addTaskRequest(new ClientRequest(requestType, value));
    }

    private void addTaskRequest(final ClientRequest request) {
        ClientRequestTask.getInstance().addRequest(request);
    }

    private void addTaskRequest(final ClientRequestType requestType, RequestDoneListener... listeners) {
        addTaskRequest(new ClientRequest(requestType), listeners);
    }

    private void addTaskRequest(final ClientRequestType requestType, final String value, RequestDoneListener... listeners) {
        addTaskRequest(new ClientRequest(requestType, value), listeners);
    }

    private void addTaskRequest(final ClientRequest request, RequestDoneListener... listeners) {
        ClientRequestTask.getInstance().addRequest(request, listeners);
    }

    public ServerResponse sendRequest(final ClientRequest request) {
        Object tmp = null;

        try {
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            while (bufferedInputStream.available() <= 0 && request_socket.isConnected())
                Thread.sleep(10);


            if (request_socket.isConnected())
                tmp = objectInputStream.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException
                | ClassCastException e) {
            e.printStackTrace();
        }

        return (ServerResponse) tmp;
    }

}
