package com.ciu196.mobilecomputing;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ClientRequestType;
import com.ciu196.mobilecomputing.common.requests.ResponseValue;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eric on 2017-10-16.
 */

public class ServerStatusFetcherTask extends LoopableTask implements RequestDoneListener {

    private final Context context;
    private final List<StatusUpdateListener> updateListeners;

    public ServerStatusFetcherTask(Context context) {
        super(1000);
        this.context = context;
        this.updateListeners = new LinkedList<>();
    }

    @Override
    protected boolean init() {
        Looper.prepare();
        return true;
    }

    @Override
    protected boolean finish() {
        return true;
    }

    @Override
    protected boolean loop() {
        ServerConnection.getInstance().fetchStatus(this);
        return true;
    }

    public void addStatusUpdateListener(final StatusUpdateListener listener) {
        updateListeners.add(listener);
    }

    @Override
    public void serverResponseReceived(ServerResponse response) {
        try {
            ResponseValue.Status status = (ResponseValue.Status) response.getValue();
            OnlineBroadcastService service = OnlineBroadcastService.getInstance();
            service.setLive(Boolean.parseBoolean(status.getStatus("broadcasting")));
            service.setBroadcasterName(status.getStatus("broadcaster"));
            service.setBroadcastStartTime(Long.parseLong(status.getStatus("broadcastStartTime")));
            service.setNumberOfListeners(Integer.parseInt(status.getStatus("nListeners")));
            for (StatusUpdateListener listener : updateListeners) listener.onStatusUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }
}
