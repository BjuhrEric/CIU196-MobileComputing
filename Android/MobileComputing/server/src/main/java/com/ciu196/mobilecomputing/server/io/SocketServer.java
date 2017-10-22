package com.ciu196.mobilecomputing.server.io;

import com.ciu196.mobilecomputing.Reaction;
import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
import com.ciu196.mobilecomputing.common.requests.ResponseValue;
import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerRequestType;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.requests.ServerResponseType;
import com.ciu196.mobilecomputing.common.tasks.TaskManager;
import com.ciu196.mobilecomputing.server.tasks.ClientConnectionCheckerTask;
import com.ciu196.mobilecomputing.server.tasks.ClientRequestFetcherTask;
import com.ciu196.mobilecomputing.server.tasks.ClientRequestHandlerTask;
import com.ciu196.mobilecomputing.server.util.Client;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.ciu196.mobilecomputing.common.Constants.DATA_PORT;
import static com.ciu196.mobilecomputing.common.Constants.REQUEST_PORT;
import static com.ciu196.mobilecomputing.common.Constants.SERVER_REQUEST_PORT;


public class SocketServer implements Server {


    private final static String NOONE = "No-one";
    private volatile long broadcastStartTime = -1;
    private volatile boolean running = false;
    private volatile Client broadcaster = null;
    private volatile String broadcasterName = NOONE;
    private ServerSocket requestSocket, serverRequestSocket, dataSocket;
    private final Map<Long, Client> connectingClients;
    private final Set<Client> connectedClients;
    private final Set<Client> listeners;

    public SocketServer() {
        System.out.println("Allocating server memory.");
        connectingClients = Collections.synchronizedMap(new TreeMap<>());
        connectedClients = Collections.synchronizedNavigableSet(new TreeSet<>());
        listeners = Collections.synchronizedNavigableSet(new TreeSet<>());
    }

    public void init() throws IOException {
        System.out.println("Opening server sockets");
        requestSocket = new ServerSocket(REQUEST_PORT);
        serverRequestSocket = new ServerSocket(SERVER_REQUEST_PORT);
        dataSocket = new ServerSocket(DATA_PORT);
        running = true;
    }

    @Override
    public void shareReaction(final Client provider, final Reaction reaction) throws IOException {
        listeners.forEach((client) -> {
            if (client != provider) {
                client.addRequest(new ServerRequest(ServerRequestType.RECEIVE_REACTION, reaction.name()));
            }
        });
        if (broadcaster != null)
            broadcaster.addRequest(new ServerRequest(ServerRequestType.RECEIVE_REACTION, reaction.name()));

        provider.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED,
                new ResponseValue.NoValue()));
    }

    public void connectRequestSocket() throws IOException {
        Socket clientSocket = requestSocket.accept();
        SocketClient client;
        BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(bis);

        Random r = new Random();
        Long rand;
        do { rand = r.nextLong(); } while(connectingClients.containsKey(rand) || rand == -1);

        client = new SocketClient(rand, clientSocket.getInetAddress());
        connectingClients.put(rand, client);
        client.bindRequestSocket(clientSocket, bis, ois, oos);

        System.out.println("Client "+rand+" connected with IP: "+clientSocket.getInetAddress().getHostAddress());
        client.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.SingleObjectValue<>(rand)));

    }

    public void connectServerRequestSocket() throws IOException {
        final Socket clientSocket = serverRequestSocket.accept();
        final BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
        final ObjectInputStream in = new ObjectInputStream(bis);
        final ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        long id;

        try {
            ClientRequest request = (ClientRequest) in.readObject();
            if (request.getType().equals(ClientRequestType.IDENTIFY)) {
                id = Long.parseLong(request.getValue());
                SocketClient client = (SocketClient) connectingClients.get(id);
                if (client != null) {
                    System.out.println("Server request socket opened for client: " + clientSocket.getInetAddress().getHostAddress());
                    client.bindServerRequestSocket(clientSocket, bis, in, out);
                    out.writeObject(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.SingleObjectValue<>(id)));
                    out.flush();

                    ClientRequestFetcherTask fetcherTask = new ClientRequestFetcherTask(client, this);
                    ClientRequestHandlerTask handlerTask = new ClientRequestHandlerTask(client, this);
                    ClientConnectionCheckerTask connectionTask = new ClientConnectionCheckerTask(client, this);

                    client.addTask(fetcherTask);
                    client.addTask(handlerTask);
                    client.addTask(connectionTask);

                    new Thread(fetcherTask, "ClientRequestFetcherThread").start();
                    new Thread(handlerTask, "ClientRequestHandlerThread").start();
                    new Thread(connectionTask, "ClientConnectionThread").start();
                    connectedClients.add(client);
                    connectingClients.remove(client.getID());
                    return;
                }
            }
            out.writeObject(new ServerResponse(ServerResponseType.REQUEST_DECLINED, new ResponseValue.NoValue()));
            out.flush();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void connectDataSocket() throws IOException {
        Socket clientSocket = dataSocket.accept();
        final ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        final ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        long id;

        try {
            ClientRequest request = (ClientRequest) in.readObject();
            if (request.getType().equals(ClientRequestType.IDENTIFY)) {
                id = Long.parseLong(request.getValue());
                SocketClient client = (SocketClient) connectingClients.get(id);
                if (client != null) {
                    System.out.println("Data socket opened for client: " + clientSocket.getInetAddress().getHostAddress());
                    client.bindDataSocket(clientSocket);
                    out.writeObject(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.SingleObjectValue<>(id)));
                    out.flush();
                    return;
                }
            }
            out.writeObject(new ServerResponse(ServerResponseType.REQUEST_DECLINED, new ResponseValue.NoValue()));
            out.flush();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void receiveData(final Client c) throws IllegalStateException, IOException {
        if (c == null || !c.equals(broadcaster))
            throw new IllegalStateException("Only the broadcaster is supposed to broadcast");

        //TODO Do something with data!
    }

    @Override
    public void sendStatus(final Client client) throws IOException {
        ResponseValue.Status status = new ResponseValue.Status();
        ServerResponse response = new ServerResponse(ServerResponseType.STATUS, status);
        status.putStatus("broadcasting", Boolean.toString(broadcaster != null));
        status.putStatus("broadcaster", broadcasterName);
        status.putStatus("nListeners", Integer.toString(listeners.size()));
        status.putStatus("broadcastStartTime", Long.toString(broadcastStartTime));

        client.sendResponse(response);
    }

    public void sendData(final Client c) throws IOException {
        c.sendData();
    }

    public void setBroadcaster(final Client c, final String name) throws IOException {
        System.out.println("Setting broadcaster to: "+name);
        if (broadcaster != null) {
            c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_DECLINED, new ResponseValue.NoValue()));
            return;
        }
        broadcasterName = name;
        broadcaster = c;
        broadcastStartTime = System.currentTimeMillis();
        listeners.remove(c);
        c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.NoValue()));
    }

    public void stopBroadcast(final Client c) throws IOException {
        stopBroadcast(c, true);
    }

    private void stopBroadcast(final Client c, boolean respond) throws IOException {
        if (c.equals(broadcaster)) {
            System.out.println("Stopping broadcast");
            broadcaster = null;
            broadcasterName = NOONE;
            broadcastStartTime = -1;
            listeners.clear();
            if (respond)
                c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.NoValue()));
        } else if (respond) {
            c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_DECLINED, new ResponseValue.NoValue()));
        }
    }

    public void detachClient(final Client c) throws IOException {
        detachClient(c, true);
    }

    public void detachClient(final Client c, boolean sendResponse) throws IOException {
        if (c != null) {
            System.out.println("Detaching client " + c.getID() + " with IP: " + c.getInetAddress().getHostAddress());
            stopBroadcast(c, false);
            removeListener(c, false);
            if (sendResponse) {
                try {
                    c.sendResponse(new ServerResponse(ServerResponseType.DETACHED, new ResponseValue.NoValue()));
                } catch (IOException e) {
                    System.out.println("The socket was already closed");
                }
            }
            connectedClients.remove(c);
            listeners.remove(c);
            c.close();

            System.out.println("Client detached");
        }
    }

    public void addListener(Client c) throws IOException {
        if (!c.equals(broadcaster) && broadcaster != null) {
            listeners.add(c);
            c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.NoValue()));
        } else {
            c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_DECLINED, new ResponseValue.NoValue()));
        }

    }

    public void removeListener(Client c) throws IOException {
        removeListener(c, true);
    }

    @Override
    public Collection<Client> getClients() {
        return connectedClients;
    }

    private void removeListener(Client c, boolean respond) throws IOException {
        listeners.remove(c);
        if (respond)
            c.sendResponse(new ServerResponse(ServerResponseType.REQUEST_ACCEPTED, new ResponseValue.NoValue()));

    }

    public void quit() {
        try {
            if (running) {
                listeners.clear();
                connectingClients.clear();
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
