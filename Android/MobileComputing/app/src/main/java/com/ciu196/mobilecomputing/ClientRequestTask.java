package com.ciu196.mobilecomputing;

import android.support.annotation.NonNull;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Eric on 2017-10-18.
 */

public final class ClientRequestTask extends LoopableTask {

    private final static ClientRequestTask instance = new ClientRequestTask();
    private Queue<Map.Entry<ClientRequest, List<RequestDoneListener>>> requests;
    private RequestHandler handler;

    private ClientRequestTask() {
        super(50);
        requests = new ConcurrentLinkedQueue<>();
    }

    public static ClientRequestTask getInstance() {
        return instance;
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    protected boolean finish() {
        return true;
    }

    @Override
    protected boolean loop() {
        if (!requests.isEmpty()) {
            Map.Entry<ClientRequest, List<RequestDoneListener>> entry = requests.poll();
            ServerResponse response = handler.handleRequest(entry.getKey());
            for (RequestDoneListener listener : entry.getValue())
                listener.serverResponseReceived(response);
        }
        return true;
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

    public void setRequestHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
