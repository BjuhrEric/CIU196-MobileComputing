package com.ciu196.mobilecomputing.server.io;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientResponse;
import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.requests.ServerRequestType;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;
import com.ciu196.mobilecomputing.common.logging.GlobalLog;
import com.ciu196.mobilecomputing.server.util.Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class SocketClient implements Client {

    private final Queue<byte[]> dataOutput;
    private final Queue<ClientRequest> clientRequests;
    private final Queue<ServerRequest> serverRequests;
    private final Collection<LoopableTask> tasks;
    private final InetAddress inetAddress;
    private final long id;
    private Socket requestSocket;
    private Socket serverRequestSocket;
    private Socket dataSocket;
    private InputStream dataInputStream;
    private OutputStream dataOutputStream;
    private BufferedInputStream bufferedInputStream1;
    private BufferedInputStream bufferedInputStream2;
    private ObjectInputStream requestInputStream;
    private ObjectInputStream responseInputStream;
    private ObjectOutputStream responseOutputStream;
    private ObjectOutputStream requestOutputStream;
    private boolean connected;
    private boolean sendingRequest = false;
    private long sendingRequestStartTime = -1;

    SocketClient(long id, InetAddress inetAddress){
        this.id = id;
        this.clientRequests = new ConcurrentLinkedQueue<>();
        this.serverRequests = new ConcurrentLinkedQueue<>();
        this.tasks = new LinkedList<>();
        this.dataOutput = new ConcurrentLinkedQueue<>();
        this.inetAddress = inetAddress;
        this.connected = true;
    }

    void addTask(final LoopableTask task) {
        tasks.add(task);
    }

    void bindRequestSocket(Socket socket, BufferedInputStream bis, ObjectInputStream ois, ObjectOutputStream oos) {
        requestSocket = socket;
        bufferedInputStream1 = bis;
        requestInputStream = ois;
        responseOutputStream = oos;
    }

    void bindDataSocket(Socket socket) throws IOException {
        dataSocket = socket;
        dataInputStream = socket.getInputStream();
        dataOutputStream = socket.getOutputStream();
    }

    void bindServerRequestSocket(Socket socket, BufferedInputStream bis, ObjectInputStream ois, ObjectOutputStream oos) {
        serverRequestSocket = socket;
        bufferedInputStream2 = bis;
        responseInputStream = ois;
        requestOutputStream = oos;
    }

    public ClientRequest getFirstRequest() {
        if (clientRequests.isEmpty())
            return null;
        return clientRequests.poll();
    }

    public void fetchRequests() throws IOException, ClassNotFoundException {
        if (!requestSocket.isClosed() && bufferedInputStream1.available() > 0) {
            final Object o = requestInputStream.readObject();
            final ClientRequest request;
            if (o != null) {
                request = (ClientRequest) o;
                clientRequests.offer(request);
            }
        }
    }

    public void sendResponse(ServerResponse response) throws IOException {
        responseOutputStream.writeObject(response);
        responseOutputStream.flush();
    }

    @Override
    public void addRequest(ServerRequest request) {
        if (!request.getType().equals(ServerRequestType.CONFIRM_CONNECTIVITY)
                || !hasRequestOfType(ServerRequestType.CONFIRM_CONNECTIVITY)) {
            serverRequests.offer(request);
        }
    }

    private boolean hasRequestOfType(ServerRequestType type) {
        for (ServerRequest request : serverRequests) {
            if (request.getType().equals(type))
                return true;
        }
        return false;
    }

    @Override
    public ClientResponse sendRequest() {
        ClientResponse response = null;
        if (serverRequests.isEmpty() || requestOutputStream == null || responseInputStream == null)
            return null;

        final ServerRequest request = serverRequests.poll();
        try {
            sendingRequest = true;
            sendingRequestStartTime = System.currentTimeMillis();
            new Thread(() -> sendRequest(request), "RequestStreamWriteThread").start();

            while (sendingRequest || bufferedInputStream2.available() <= 0)
                if (sendingRequestStartTime + 15000 < System.currentTimeMillis())
                    throw new IOException("Request timed out, assuming connection lost");
                else
                    Thread.sleep(10);

            response = (ClientResponse) responseInputStream.readObject();
        } catch (IOException e) {
            setConnected(false);
        } catch (ClassNotFoundException | InterruptedException e) {
            if (request.getType().equals(ServerRequestType.CONFIRM_CONNECTIVITY))
                setConnected(false);
            else
                GlobalLog.log(e);
        }
        return response;
    }

    private void sendRequest(ServerRequest request) {
        try {
            requestOutputStream.writeObject(request);
            requestOutputStream.flush();
            sendingRequestStartTime = System.currentTimeMillis();
            sendingRequest = false;
        } catch (IOException e) {
            sendingRequest = true;
            sendingRequestStartTime = Long.MIN_VALUE;
            // This will force an log below, causing the server to detach the client
        }
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    private void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public long getID() {
        return id;
    }

    public synchronized byte[] readData() throws IOException {
        byte[] buffer = new byte[dataInputStream.available()];
        dataInputStream.read(buffer);
        return buffer;
    }

    public void sendData() throws IOException {
        dataOutputStream.write(dataOutput.poll());
        dataOutputStream.flush();
    }

    @Override
    public void addOutputData(byte[] data) {
        dataOutput.offer(data);
    }

    public void close() throws IOException {
        if (dataSocket != null)
            dataSocket.close();
        if (requestSocket != null)
            requestSocket.close();
        if (serverRequestSocket != null)
            serverRequestSocket.close();

        tasks.forEach(LoopableTask::stop);
        tasks.clear();
        serverRequests.clear();
        clientRequests.clear();
        dataOutput.clear();
    }

    @Override
    public int compareTo(Client client) {
        return Long.compare(id, client.getID());
    }
}
