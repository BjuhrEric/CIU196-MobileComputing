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

    private volatile boolean running = false;
    private volatile Client broadcaster = null;
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

    public synchronized void connectRequestSocket() throws IOException {
        Socket clientSocket = requestSocket.accept();
        SocketClient client = (SocketClient) clientMap.get(clientSocket.getInetAddress());
        if (client == null) {
            client = new SocketClient();
            client.bindRequestSocket(clientSocket);
            clientMap.put(clientSocket.getInetAddress(), client);
            listeners.add(client);
        } else {
            System.out.println("Client connected: "+clientSocket.getInetAddress().getHostAddress());
            client.bindRequestSocket(clientSocket);

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

    }

    public synchronized void connectDataSocket() throws IOException {
        Socket clientSocket = dataSocket.accept();
        SocketClient client = (SocketClient) clientMap.get(clientSocket.getInetAddress());
        if (client == null) {
            client = new SocketClient();
            client.bindDataSocket(clientSocket);
            clientMap.put(clientSocket.getInetAddress(), client);
            listeners.add(client);
        } else {
            System.out.println("Client connected: "+clientSocket.getInetAddress().getHostAddress());
            client.bindDataSocket(clientSocket);

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
    }

    public void receiveData(Client c) throws IllegalStateException, IOException {
        if (c == null || !c.equals(broadcaster))
            throw new IllegalStateException("Only the broadcaster is supposed to broadcast");

        c.sendMessage(ServerRequest.SEND_BROADCAST);
        byte[] data = c.readData();

        listeners.forEach((client) -> client.addOutputData(data));
        //TODO Do something with data!
    }

    @Override
    public void sendStatus(Client client) throws IOException {
        ServerResponse.Status status = new ServerResponse.Status();
        ServerResponse response = new ServerResponse(ServerResponse.ResponseType.STATUS, status);
        status.putStatus("broadcasting", Boolean.toString(broadcaster != null));
        status.putStatus("nListeners", Integer.toString(listeners.size()));

        client.sendMessage(response);
    }

    public void sendData(Client c) throws IOException {
        c.sendData();
    }

    public void setBroadcaster(Client c) throws IOException {
        System.out.println("Setting broadcaster");
        if (broadcaster != null) {
            c.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_DECLINED, new ServerResponse.NoValue()));
            return;
        }
        broadcaster = c;
        listeners.remove(c);
        c.sendMessage(new ServerResponse(ServerResponse.ResponseType.REQUEST_ACCEPTED, new ServerResponse.NoValue()));
    }

    public void detachClient(Client c) throws IOException {
        if (c == null)
            throw new IllegalArgumentException("Client may not be null");
        if (c.equals(broadcaster))
            broadcaster = null;

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
