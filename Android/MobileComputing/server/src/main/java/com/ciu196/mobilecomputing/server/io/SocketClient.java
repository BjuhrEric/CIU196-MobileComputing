package com.ciu196.mobilecomputing.server.io;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerMessage;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;
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
    private final Queue<ClientRequest> requests;
    private final Collection<LoopableTask> tasks;
    private InetAddress inetAddress = null;
    private Socket requestSocket, dataSocket;
    private InputStream dataInputStream;
    private ObjectInputStream requestInputStream;
    private BufferedInputStream bufferedInputStream;
    private OutputStream dataOutputStream;
    private ObjectOutputStream requestOutputStream;

    SocketClient(){
        this.requests = new ConcurrentLinkedQueue<>();
        this.tasks = new LinkedList<>();
        this.dataOutput = new ConcurrentLinkedQueue<>();
    }

    void addTask(final LoopableTask task) {
        tasks.add(task);
    }

    void bindRequestSocket(Socket socket) throws IOException {
        requestSocket = socket;
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        requestInputStream = new ObjectInputStream(bufferedInputStream);
        requestOutputStream = new ObjectOutputStream(socket.getOutputStream());
        inetAddress = socket.getInetAddress();
    }

    void bindDataSocket(Socket socket) throws IOException {
        dataSocket = socket;
        dataInputStream = socket.getInputStream();
        dataOutputStream = socket.getOutputStream();
        inetAddress = socket.getInetAddress();
    }

    public ClientRequest getFirstRequest() {
        if (requests.isEmpty())
            return null;
        return requests.poll();
    }

    public void fetchRequests() throws IOException, ClassNotFoundException {
        if (bufferedInputStream.available() > 0) {
            final Object o = requestInputStream.readObject();
            final ClientRequest request;
            if (o != null) {
                request = (ClientRequest) o;
                requests.offer(request);
            }
        }
    }

    public void sendMessage(ServerMessage response) throws IOException {
        requestOutputStream.writeObject(response);
        requestOutputStream.flush();
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public boolean isConnected() {
        return dataSocket != null && requestSocket != null && dataSocket.isConnected()
                && requestSocket.isConnected();
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
        dataSocket.close();
        requestSocket.close();
        tasks.forEach(LoopableTask::stop);
    }

    @Override
    public int compareTo(Client client) {
        return inetAddress.getHostAddress().compareTo(client.getInetAddress().getHostAddress());
    }
}
