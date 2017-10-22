package com.ciu196.mobilecomputing.tasks;

import android.content.Context;
import android.os.Looper;

import com.ciu196.mobilecomputing.common.requests.ResponseValue;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;
import com.ciu196.mobilecomputing.io.OnlineBroadcastService;
import com.ciu196.mobilecomputing.io.RequestDoneListener;
import com.ciu196.mobilecomputing.io.ServerConnection;

/**
 * Created by Eric on 2017-10-16.
 */

public class ServerStatusFetcherTask extends LoopableTask implements RequestDoneListener {

    private final Context context;

    public ServerStatusFetcherTask(Context context) {
        super(1000);
        this.context = context;
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

    @Override
    public void serverResponseReceived(ServerResponse response) {
        try {
            ResponseValue.Status status = (ResponseValue.Status) response.getValue();
            OnlineBroadcastService service = OnlineBroadcastService.getInstance();
            service.setLive(Boolean.parseBoolean(status.getStatus("broadcasting")));
            service.setBroadcasterName(status.getStatus("broadcaster"));
            service.setBroadcastStartTime(Long.parseLong(status.getStatus("broadcastStartTime")));
            service.setNumberOfListeners(Integer.parseInt(status.getStatus("nListeners")));
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }
}
