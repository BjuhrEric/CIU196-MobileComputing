package com.ciu196.mobilecomputing.servertestclient;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
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
        System.out.println("Starting application");
        Application app = new Application();
        try {
            app.getStatus();
            app.startBroadcast();
            app.getStatus();
            app.stopBroadcast();
            app.startListen();
            app.getStatus();
            app.stopListen();
            app.detach();

            app.request_socket.close();
            app.data_socket.close();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Application finished");
    }

    private Application() {
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

    private void startBroadcast() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Starting broadcast");
        serverMessage = sendRequest(ClientRequestType.BROADCAST, "Ray Charles");

        if (serverMessage != null) {
            System.out.println("Response from server: " + ((ServerResponse) serverMessage).getType());
        }
    }

    private void stopBroadcast() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Stopping broadcast");
        serverMessage = sendRequest(ClientRequestType.STOP_BROADCAST, "Ray Charles");

        if (serverMessage != null) {
            System.out.println("Response from server: " + ((ServerResponse) serverMessage).getType());
        }
    }

    private void startListen() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Starting to listen");
        serverMessage = sendRequest(ClientRequestType.LISTEN, "Ray Charles");

        if (serverMessage != null) {
            System.out.println("Response from server: " + ((ServerResponse) serverMessage).getType());
        }
    }

    private void stopListen() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Stopping listening");
        serverMessage = sendRequest(ClientRequestType.STOP_LISTEN, "Ray Charles");

        if (serverMessage != null) {
            System.out.println("Response from server: " + ((ServerResponse) serverMessage).getType());
        }
    }

    private void detach() throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;

        System.out.println("Detaching application");

        serverMessage = sendRequest(ClientRequestType.DETACH_CLIENT);
        if (serverMessage != null)
            System.out.println("Response from server: " + ((ServerResponse) serverMessage).getType());
    }

    private void getStatus()  throws IOException, InterruptedException, ClassNotFoundException {
        ServerMessage serverMessage;
        ServerResponse serverResponse;
        System.out.println("Fetching status from server.");

        serverMessage = sendRequest(ClientRequestType.REQUEST_STATUS);

        if (serverMessage != null && serverMessage.getClass().equals(ServerResponse.class)) {
            serverResponse = (ServerResponse) serverMessage;
            if (serverResponse.getType() == ServerResponse.ResponseType.STATUS) {
                ServerResponse.Status status = (ServerResponse.Status) serverResponse.getValue();
                System.out.println("Broadcasting: "+status.getStatus("broadcasting"));
                System.out.println("Broadcaster: "+status.getStatus("broadcaster"));
                System.out.println("Broadcast start time: "+status.getStatus("broadcastStartTime"));
                System.out.println("Number of listeners: "+status.getStatus("nListeners"));
            }
        }
    }

    private ServerMessage sendRequest(final ClientRequestType request) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
        return sendRequest(new ClientRequest(request));
    }

    private ServerMessage sendRequest(final ClientRequestType request, final String val) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
        return sendRequest(new ClientRequest(request, val));
    }

    private ServerMessage sendRequest(final ClientRequest request) throws IOException,
            InterruptedException, ClassNotFoundException, ClassCastException {
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
