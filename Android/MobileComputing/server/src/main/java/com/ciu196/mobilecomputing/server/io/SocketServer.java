package com.ciu196.mobilecomputing.server.io;

import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.TaskManager;
import com.ciu196.mobilecomputing.server.tasks.ClientConnectionCheckerTask;
import com.ciu196.mobilecomputing.server.tasks.ClientRequestFetcherTask;
import com.ciu196.mobilecomputing.server.tasks.ClientRequestHandlerTask;
import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import static com.ciu196.mobilecomputing.common.Constants.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class SocketServer implements Server {


    private final static String NOONE = "No-one";
    private volatile long broadcastStartTime = -1;
    private volatile boolean running = false;
    private volatile Client broadcaster = null;
    private volatile String broadcasterName = NOONE;
    private ServerSocket requestSocket, dataSocket;
    private final Map<InetAddress, Client> clientMap;
    private final Set<Client> listeners;

    public SocketServer() {
        System.out.println("Allocating server memory.");
        clientMap = new HashMap<>();
        listeners = new TreeSet<>();
    }

    public void init() throws IOException {
        System.out.println("Opening server sockets");
        requestSocket = new ServerSocket(REQUEST_PORT);
        dataSocket = new ServerSocket(DATA_PORT);
        running = true;
    }

    public void connectRequestSocket() throws IOException {
        Socket clientSocket = requestSocket.accept();
        SocketClient client;

        if (clientMap.containsKey(clientSocket.getInetAddress())) {
            client = (SocketClient) clientMap.get(clientSocket.getInetAddress());
            detachClient(client);
        } else {
            client = new SocketClient();
        }

        System.out.println("Client connected: "+clientSocket.getInetAddress().getHostAddress());
        client.bindRequestSocket(clientSocket);
        client.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_ACCEPTED, new ServerResponse.NoValue()));

        clientMap.put(clientSocket.getInetAddress(), client);
        listeners.add(client);

        ClientRequestFetcherTask fetcherTask = new ClientRequestFetcherTask(client, this);
        ClientRequestHandlerTask handlerTask = new ClientRequestHandlerTask(client, this);
        ClientConnectionCheckerTask connectionTask = new ClientConnectionCheckerTask(client, this);

        client.addTask(fetcherTask);
        client.addTask(handlerTask);
        client.addTask(connectionTask);

        new Thread(fetcherTask).start();
        new Thread(handlerTask).start();
        new Thread(connectionTask).start();
    }

    public void connectDataSocket() throws IOException {
        Socket clientSocket = dataSocket.accept();
        SocketClient client = (SocketClient) clientMap.get(clientSocket.getInetAddress());
        if (client != null) {
            System.out.println("Data socket opened for client: "+clientSocket.getInetAddress().getHostAddress());
            client.bindDataSocket(clientSocket);
        }
    }

    public void receiveData(final Client c) throws IllegalStateException, IOException {
        if (c == null || !c.equals(broadcaster))
            throw new IllegalStateException("Only the broadcaster is supposed to broadcast");

        c.sendMessage(ServerRequest.SEND_BROADCAST);
        byte[] data = c.readData();

        listeners.forEach((client) -> client.addOutputData(data));
        //TODO Do something with data!
    }

    @Override
    public void sendStatus(final Client client) throws IOException {
        System.out.println("Sending status to client");
        ServerResponse.Status status = new ServerResponse.Status();
        ServerResponse response = new ServerResponse(ServerResponse.ResponseType.STATUS, status);
        status.putStatus("broadcasting", Boolean.toString(broadcaster != null));
        status.putStatus("broadcaster", broadcasterName);
        status.putStatus("nListeners", Integer.toString(listeners.size()));
        status.putStatus("broadcastStartTime", Long.toString(broadcastStartTime));

        client.sendMessage(response);
    }

    public void sendData(final Client c) throws IOException {
        c.sendData();
    }

    public void setBroadcaster(final Client c, final String name) throws IOException {
        System.out.println("Setting broadcaster");
        if (broadcaster != null) {
            c.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_DECLINED, new ServerResponse.NoValue()));
            return;
        }
        broadcasterName = name;
        broadcaster = c;
        broadcastStartTime = System.currentTimeMillis();
        listeners.remove(c);
        c.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_ACCEPTED, new ServerResponse.NoValue()));
    }

    public void detachClient(final Client c) throws IOException {
        if (c == null)
            throw new IllegalArgumentException("Client may not be null");
        if (c.equals(broadcaster)) {
            broadcaster = null;
            broadcasterName = NOONE;
            broadcastStartTime = -1;
        }

        System.out.println("Client detached");
        c.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_ACCEPTED, new ServerResponse.NoValue()));
        clientMap.remove(c.getInetAddress());
        listeners.remove(c);
        c.close();
    }

    public void quit() {
        try {
            if (running) {
                listeners.clear();
                clientMap.clear();
                requestSocket.close();
                dataSocket.close();
                running = false;
            }
            TaskManager.getInstance().finishAllTasks();
            System.out.println("Server has been shut down");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
