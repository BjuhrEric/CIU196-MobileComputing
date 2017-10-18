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

    public boolean startBroadcast() {
        ServerResponse serverMessage = null;
        try {
            serverMessage = sendRequest(ClientRequestType.BROADCAST, "Ray Charles");
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public boolean stopBroadcast() {
        ServerResponse serverMessage = null;
        try {
            serverMessage = sendRequest(ClientRequestType.STOP_BROADCAST);
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public boolean startListen() {
        ServerResponse serverMessage = null;
        try {
            serverMessage = sendRequest(ClientRequestType.LISTEN, "Ray Charles");
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public boolean stopListen() {
        ServerResponse serverMessage = null;
        try {
            serverMessage = sendRequest(ClientRequestType.STOP_LISTEN, "Ray Charles");
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return serverMessage.getType().equals(ServerResponse.ResponseType.REQUEST_ACCEPTED);
    }

    public boolean detach() throws IOException, InterruptedException, ClassNotFoundException {
        ServerResponse serverMessage = sendRequest(ClientRequestType.DETACH_CLIENT);
        return serverMessage.getType().equals(ServerResponse.ResponseType.DETACHED);
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
