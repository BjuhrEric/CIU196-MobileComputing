package com.ciu196.mobilecomputing.server.tasks;

import com.ciu196.mobilecomputing.common.tasks.LoopableTask;
import com.ciu196.mobilecomputing.common.tasks.TaskManager;
import com.ciu196.mobilecomputing.server.util.Server;

abstract class ServerTask extends LoopableTask {

    protected final Server server;

    ServerTask(final Server server) {
        this(server, 250);
    }

    ServerTask(final Server server, final long delay) {
        super(delay);
        this.server = server;
        setTaskStateListener(TaskManager.getInstance());
    }

}
