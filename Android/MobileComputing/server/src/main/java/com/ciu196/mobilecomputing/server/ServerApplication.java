package com.ciu196.mobilecomputing.server;

import com.ciu196.mobilecomputing.server.io.SocketServer;
import com.ciu196.mobilecomputing.server.tasks.ConnectClientDataSocketTask;
import com.ciu196.mobilecomputing.server.tasks.ConnectClientRequestSocketTask;
import com.ciu196.mobilecomputing.common.tasks.TaskManager;
import com.ciu196.mobilecomputing.server.tasks.ConnectClientServerRequestSocketTask;
import com.ciu196.mobilecomputing.server.tasks.SendServerRequestsTask;
import com.ciu196.mobilecomputing.server.util.Server;

import java.io.IOException;

public final class ServerApplication {

    private final Server server;

    public static void main(final String[] args) {
        System.out.println("Starting server...");
        final ServerApplication application = new ServerApplication();
        application.init();
        application.start();
        System.out.println("Server started!");
        Runtime.getRuntime().addShutdownHook(new Thread(application.server::quit));
    }

    private ServerApplication() {
        server = new SocketServer();
    }

    private void init() {
        try {
            server.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        new Thread(new ConnectClientDataSocketTask(server)).start();
        new Thread(new ConnectClientRequestSocketTask(server)).start();
        new Thread(new ConnectClientServerRequestSocketTask(server)).start();
        new Thread(new SendServerRequestsTask(server)).start();
    }

}
