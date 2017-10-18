package com.ciu196.mobilecomputing;

import android.support.annotation.NonNull;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
import com.ciu196.mobilecomputing.common.requests.ClientResponse;
import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.requests.ServerResponseType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.ciu196.mobilecomputing.common.Constants.DATA_PORT;
import static com.ciu196.mobilecomputing.common.Constants.REQUEST_PORT;
import static com.ciu196.mobilecomputing.common.Constants.SERVER_REQUEST_PORT;

public class ServerConnection {

    private final static ServerConnection instance = new ServerConnection();
    private static boolean singletonInstantiated = false;

    private Socket data_socket, request_socket, server_request_socket;
    private BufferedInputStream bufferedInputStream, bufferedInputStream2;
    private ObjectOutputStream requestOutputStream, responseOutputStream;
    private ObjectInputStream responseInputStream, requestInputStream;

    private Queue<Map.Entry<ClientRequest, List<RequestDoneListener>>> requests;


    private ServerConnection() {
        if (singletonInstantiated) throw new UnsupportedOperationException("Use singleton");
        singletonInstantiated = true;

        requests = new LinkedList<>();

        try {
            request_socket = new Socket(InetAddress.getByName("46.239.104.32"), REQUEST_PORT);
            bufferedInputStream = new BufferedInputStream(request_socket.getInputStream());
            requestOutputStream = new ObjectOutputStream(request_socket.getOutputStream());
            responseInputStream = new ObjectInputStream(bufferedInputStream);
            while (bufferedInputStream.available() <= 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

            ServerResponse response = (ServerResponse) responseInputStream.readObject(); //Confirmation of connection.
            if (response.getType() == ServerResponseType.DETACHED) {
                // Detached from previous session, wait for confirmation for the new session.
                while (bufferedInputStream.available() <= 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                responseInputStream.readObject();
            }

            data_socket = new Socket(InetAddress.getByName("46.239.104.32"), DATA_PORT);
            server_request_socket = new Socket(InetAddress.getByName("46.239.104.32"), SERVER_REQUEST_PORT);
            bufferedInputStream2 = new BufferedInputStream(server_request_socket.getInputStream());
            responseOutputStream = new ObjectOutputStream(server_request_socket.getOutputStream());
            requestInputStream = new ObjectInputStream(bufferedInputStream2);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ServerConnection getInstance() {
        return instance;
    }

    public void startBroadcast(final String name) {
        addRequest(ClientRequestType.BROADCAST, name);
    }

    public void stopBroadcast() {
        addRequest(ClientRequestType.STOP_BROADCAST);
    }

    public void startListen() {
        addRequest(ClientRequestType.LISTEN);
    }

    public void stopListen() {
        addRequest(ClientRequestType.STOP_LISTEN);
    }

    public void startBroadcast(final String name, RequestDoneListener... listeners) {
        addRequest(ClientRequestType.BROADCAST, name, listeners);
    }

    public void stopBroadcast(RequestDoneListener... listeners) {
        addRequest(ClientRequestType.STOP_BROADCAST, listeners);
    }

    public void startListen(RequestDoneListener... listeners) {
        addRequest(ClientRequestType.LISTEN, listeners);
    }

    public void stopListen(RequestDoneListener... listeners) {
        addRequest(ClientRequestType.STOP_LISTEN, listeners);
    }

    public void sendReaction(Reaction reaction){
        addRequest(ClientRequestType.SEND_REACTION, reaction.toString());
    }

    public void fetchStatus(RequestDoneListener... listeners) {
        addRequest(ClientRequestType.REQUEST_STATUS, listeners);
    }

    public void detach() throws IOException, InterruptedException, ClassNotFoundException {
        addRequest(ClientRequestType.DETACH_CLIENT);
    }

    private void addRequest(final ClientRequestType requestType) {
        addRequest(new ClientRequest(requestType));
    }

    private void addRequest(final ClientRequestType requestType, final String value) {
        addRequest(new ClientRequest(requestType, value));
    }

    private void addRequest(final ClientRequestType requestType, RequestDoneListener... listeners) {
        addRequest(new ClientRequest(requestType), listeners);
    }

    private void addRequest(final ClientRequestType requestType, final String value, RequestDoneListener... listeners) {
        addRequest(new ClientRequest(requestType, value), listeners);
    }

    public void addRequest(ClientRequest request) {
        addRequest(request, new LinkedList<>());
    }

    public void addRequest(ClientRequest request, @NonNull RequestDoneListener... listeners) {
        List<RequestDoneListener> l = new LinkedList<>();
        l.addAll(Arrays.asList(listeners));
        addRequest(request, l);
    }

    public void addRequest(ClientRequest request, @NonNull List<RequestDoneListener> listeners) {
        requests.offer(new AbstractMap.SimpleEntry<>(request, listeners));
    }

    public ServerResponse sendRequest() {
        Object tmp = null;
        if (!requests.isEmpty()) {
            Map.Entry<ClientRequest, List<RequestDoneListener>> request = requests.poll();
            try {
                requestOutputStream.writeObject(request.getKey());
                requestOutputStream.flush();
                while (bufferedInputStream.available() <= 0)
                    Thread.sleep(10);

                tmp = responseInputStream.readObject();

                for (RequestDoneListener listener : request.getValue())
                    listener.serverResponseReceived((ServerResponse) tmp);
            } catch (IOException | InterruptedException | ClassNotFoundException
                    | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return (ServerResponse) tmp;
    }

    public ServerRequest fetchRequest() {
        try {
            if (bufferedInputStream2.available() > 0) {
                final Object o = requestInputStream.readObject();
                final ClientRequest request;
                if (o != null) {
                    return (ServerRequest) o;
                }
            }
        } catch (IOException | ClassNotFoundException e) {

        }
        return null;
    }

    public void sendResponse(final ClientResponse response) {
        try {
            responseOutputStream.writeObject(response);
            responseOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
