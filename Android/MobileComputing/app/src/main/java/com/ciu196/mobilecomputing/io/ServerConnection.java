package com.ciu196.mobilecomputing.io;

import android.support.annotation.NonNull;

import com.ciu196.mobilecomputing.Reaction;
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

import static com.ciu196.mobilecomputing.common.Constants.REQUEST_PORT;
import static com.ciu196.mobilecomputing.common.Constants.SERVER_REQUEST_PORT;

public class ServerConnection {

    private final static ServerConnection instance = new ServerConnection();
    private static boolean singletonInstantiated = false;

    private Socket request_socket, server_request_socket;
    private BufferedInputStream bufferedInputStream, bufferedInputStream2, bufferedInputStream3;
    private ObjectOutputStream requestOutputStream, responseOutputStream;
    private ObjectInputStream responseInputStream, requestInputStream;
    private boolean request_socket_init = false, server_request_socket_init = false;

    private Queue<Map.Entry<ClientRequest, List<RequestDoneListener>>> requests;


    private ServerConnection() {
        if (singletonInstantiated) throw new UnsupportedOperationException("Use singleton");
        singletonInstantiated = true;

        requests = new LinkedList<>();

        try {
            long id = initRequestSocket();
            if (id == -1)
                throw new IOException("Could not establish connection on port: "+REQUEST_PORT);
            request_socket_init = true;


            id = initServerRequestSocket(id);
            if (id == -1)
                throw new IOException("Could not establish connection on port: "+SERVER_REQUEST_PORT);
            server_request_socket_init = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long initRequestSocket() throws IOException {
        try {
            request_socket = new Socket(InetAddress.getByName("46.239.104.32"), REQUEST_PORT);
            bufferedInputStream = new BufferedInputStream(request_socket.getInputStream());
            requestOutputStream = new ObjectOutputStream(request_socket.getOutputStream());
            responseInputStream = new ObjectInputStream(bufferedInputStream);
            ServerResponse response;

            while (bufferedInputStream.available() <= 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

            response = (ServerResponse) responseInputStream.readObject(); //Confirmation of connection.

            if (response.getType().equals(ServerResponseType.REQUEST_ACCEPTED))
                return (Long) response.getValue().getValue();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private long initServerRequestSocket(long id) throws IOException {
        server_request_socket = new Socket(InetAddress.getByName("46.239.104.32"), SERVER_REQUEST_PORT);
        bufferedInputStream2 = new BufferedInputStream(server_request_socket.getInputStream());
        responseOutputStream = new ObjectOutputStream(server_request_socket.getOutputStream());
        requestInputStream = new ObjectInputStream(bufferedInputStream2);
        ServerResponse response;

        addRequest(ClientRequestType.IDENTIFY, Long.toString(id));
        response = sendRequest(bufferedInputStream2, responseOutputStream, requestInputStream);

        if (response.getType().equals(ServerResponseType.REQUEST_ACCEPTED))
            return (Long) response.getValue().getValue();

        return -1;
    }

    public static ServerConnection getInstance() {
        return instance;
    }

    public void stopBroadcast() {
        addRequest(ClientRequestType.STOP_BROADCAST);
    }

    public void stopListen() {
        addRequest(ClientRequestType.STOP_LISTEN);
    }

    public void startBroadcast(final String name, RequestDoneListener... listeners) {
        addRequest(ClientRequestType.BROADCAST, name, listeners);
    }

    public void startListen(RequestDoneListener... listeners) {
        addRequest(ClientRequestType.LISTEN, listeners);
    }

    public void sendReaction(Reaction reaction){
        addRequest(ClientRequestType.SEND_REACTION, reaction.name());
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
        if (request_socket_init)
            return sendRequest(bufferedInputStream, requestOutputStream, responseInputStream);
        return null;
    }

    private ServerResponse sendRequest(final BufferedInputStream bis, final ObjectOutputStream oos,
                                       final ObjectInputStream ois) {
        Object tmp = null;
        if (!requests.isEmpty()) {
            Map.Entry<ClientRequest, List<RequestDoneListener>> request = requests.poll();
            try {
                oos.writeObject(request.getKey());
                oos.flush();
                while (bis.available() <= 0)
                    Thread.sleep(10);

                tmp = ois.readObject();

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
        if (server_request_socket_init) {
            try {
                if (bufferedInputStream2.available() > 0) {
                    final Object o = requestInputStream.readObject();
                    if (o != null) {
                        return (ServerRequest) o;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendResponse(final ClientResponse response) {
        if (server_request_socket_init) {
            try {
                responseOutputStream.writeObject(response);
                responseOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
