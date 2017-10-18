package com.ciu196.mobilecomputing;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ciu196.mobilecomputing.common.requests.ServerResponse;
import com.ciu196.mobilecomputing.common.tasks.LoopableTask;

import java.io.IOException;

/**
 * Created by Eric on 2017-10-16.
 */

public class ServerStatusFetcherTask extends LoopableTask {

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
        try {
            Log.d("ServerStatusFetcher", "Fetching server status");
            ServerResponse.Status status = ServerConnection.getInstance().getStatus();
            OnlineBroadcastService service = OnlineBroadcastService.getInstance();
            service.setLive(Boolean.parseBoolean(status.getStatus("broadcasting")));
            service.setBroadcasterName(status.getStatus("broadcaster"));
            service.setBroadcastStartTime(Long.parseLong(status.getStatus("broadcastStartTime")));
            service.setNumberOfListeners(Integer.parseInt(status.getStatus("nListeners")));
        } catch (IOException | InterruptedException | ClassCastException | ClassNotFoundException e) {
            Toast.makeText(context, "Error occurred while fetching server status", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
