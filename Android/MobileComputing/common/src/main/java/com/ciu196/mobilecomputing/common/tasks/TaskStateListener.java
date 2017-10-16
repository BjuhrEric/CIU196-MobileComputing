package com.ciu196.mobilecomputing.common.tasks;

/**
 * Created by Eric on 2017-10-12.
 */

public interface TaskStateListener {

    void taskInitiated(LoopableTask task, boolean success);
    void taskFinished(LoopableTask task, boolean success);

}
