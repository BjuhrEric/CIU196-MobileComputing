package com.ciu196.mobilecomputing.common.tasks;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class TaskManager implements TaskStateListener {
    private static final TaskManager taskManager = new TaskManager();
    private static boolean created = false;

    private final Set<LoopableTask> runningTasks;

    public static TaskManager getInstance() {
        return taskManager;
    }

    private TaskManager() {
        if (created)
            throw new UnsupportedOperationException("Singleton. Use getInstance instead.");
        created = true;
        runningTasks = Collections.synchronizedSortedSet(new TreeSet<>());
    }

    public void finishAllTasks() {
        runningTasks.forEach(LoopableTask::stop);
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
