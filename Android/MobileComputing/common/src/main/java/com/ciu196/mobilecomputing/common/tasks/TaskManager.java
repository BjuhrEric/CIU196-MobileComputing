package com.ciu196.mobilecomputing.common.tasks;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public final class TaskManager implements TaskStateListener {
    private static final TaskManager taskManager = new TaskManager();
    private static boolean created = false;

    private final SortedSet<LoopableTask> runningTasks;

    public static TaskManager getInstance() {
        return taskManager;
    }

    private TaskManager() {
        if (created)
            throw new UnsupportedOperationException("Singleton. Use getInstance instead.");
        created = true;
        runningTasks = Collections.synchronizedSortedSet(new TreeSet<LoopableTask>());
    }

    public void finishAllTasks() {
        for (LoopableTask task : runningTasks) {
         task.stop();
        }
    }

    @Override
    public void taskInitiated(final LoopableTask task, boolean success) {
        if (success)
            runningTasks.add(task);
    }

    @Override
    public void taskFinished(final LoopableTask task, final boolean success) {
        runningTasks.remove(task);
        if (!success) {
            //TODO Handle failures
        }
    }
}
