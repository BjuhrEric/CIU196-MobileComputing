package com.ciu196.mobilecomputing.common.tasks;

import com.ciu196.mobilecomputing.common.logging.GlobalLog;

/**
 * Created by Eric on 2017-10-12.
 */

public abstract class LoopableTask implements Runnable, Comparable<LoopableTask> {

    private boolean running;
    private final long startTime;
    private final long delay;
    private TaskStateListener listener;

    protected LoopableTask(long delay) {
        this.startTime = System.currentTimeMillis();
        this.delay = delay;
        running = false;
    }

    @Override
    public final int compareTo(LoopableTask serverTask) {
        return Long.compare(startTime, serverTask.startTime);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        running = init();
        if (listener != null)
            listener.taskInitiated(this, running);

        try {
            while (running) {
                running = loop();
                if (delay > 0)
                    Thread.sleep(delay);
            }
        } catch (InterruptedException e) {
            GlobalLog.log(e);
        }

        if (listener != null)
            listener.taskFinished(this, finish());
    }

    public void setTaskStateListener(TaskStateListener listener) {
        this.listener = listener;
    }

    protected abstract boolean init();
    protected abstract boolean finish();
    protected abstract boolean loop();
}
