package com.ciu196.mobilecomputing.servertestclient;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerMessage;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static com.ciu196.mobilecomputing.common.Constants.*;

public class Application {

    private Socket data_socket;
    private Socket request_socket;
    private BufferedInputStream bufferedInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public static void main(String[] args) {
        new Application();
    }

    private Application() {
        try {
            data_socket = new Socket(InetAddress.getLocalHost(), DATA_PORT);
            request_socket = new Socket(InetAddress.getLocalHost(), REQUEST_PORT);
            bufferedInputStream = new BufferedInputStream(request_socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(request_socket.getOutputStream());
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            System.out.println("Starting application");
            //startBroadcast();
            getStatus();
            detach();

            request_socket.close();
            data_socket.close();
            System.out.println("Application finished");
        } catch (InterruptedException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startBroadcast() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Starting broadcast");
        serverMessage = sendRequest(ClientRequest.BROADCAST);

        if (serverMessage != null) {
            System.out.println("Response from server: "+ serverMessage.toString());
        }
    }

    private void detach() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Detaching application");

        serverMessage = sendRequest(ClientRequest.DETACH_CLIENT);
        if (serverMessage != null)
            System.out.println("Response from server: " + serverMessage.toString());
    }

    private void getStatus()  throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;
        ServerResponse serverResponse;
        System.out.println("Fetching status from server.");

        serverMessage = sendRequest(ClientRequest.REQUEST_STATUS);

        if (serverMessage != null && serverMessage.getClass().equals(ServerResponse.class)) {
            serverResponse = (ServerResponse) serverMessage;
            if (serverResponse.getType() == ServerResponse.ResponseType.STATUS) {
                ServerResponse.Status status = (ServerResponse.Status) serverResponse.getValue();
                System.out.println("Broadcasting: "+status.getStatus("broadcasting"));
            }
        }
    }

    private ServerMessage sendRequest(final ClientRequest request) throws IOException, InterruptedException, ClassNotFoundException, ClassCastException {
        Object o = null;

        objectOutputStream.writeObject(request);
        objectOutputStream.flush();


        while (bufferedInputStream.available() <= 0 && request_socket.isConnected())
            Thread.sleep(10);


        if (request_socket.isConnected())
            o = objectInputStream.readObject();

        return (ServerMessage) o;
    }

}