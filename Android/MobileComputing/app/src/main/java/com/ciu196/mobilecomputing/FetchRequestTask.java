package com.ciu196.mobilecomputing;

import android.util.Log;

import com.ciu196.mobilecomputing.common.requests.ClientResponse;
import com.ciu196.mobilecomputing.common.requests.ClientResponseType;
import com.ciu196.mobilecomputing.common.requests.ResponseValue;
import com.ciu196.mobilecomputing.common.requests.ServerRequest;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eric on 2017-10-18.
 */

public class FetchRequestTask extends LoopableTask {

    private final static FetchRequestTask instance = new FetchRequestTask();

    private FetchRequestTask() {
        super(100);
    }

    public static FetchRequestTask getInstance() {
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
        ServerRequest request = ServerConnection.getInstance().fetchRequest();
        if (request != null) {
            handleRequest(request);
        }
        return true;
    }

    private void handleRequest(final ServerRequest request) {
        switch(request.getType()) {
            case CONFIRM_CONNECTIVITY:
                ServerConnection.getInstance().sendResponse(new ClientResponse(ClientResponseType
                        .CONNECTIVITY_CONFIRMED, new ResponseValue.NoValue()));
                break;
            case RECEIVE_REACTION:
                Log.d("Reactions", "Reaction received");
                ReactionService.onReactionReceived(Reaction.valueOf(request.getValue()));
                ServerConnection.getInstance().sendResponse(new ClientResponse(ClientResponseType
                        .REQUEST_ACCEPTED, new ResponseValue.NoValue()));
                break;
            case RECEIVE_BROADCAST: //TODO
            case SEND_BROADCAST:
                break;
        }
    }
}
