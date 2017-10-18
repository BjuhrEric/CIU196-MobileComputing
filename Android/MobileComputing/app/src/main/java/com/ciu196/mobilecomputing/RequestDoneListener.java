package com.ciu196.mobilecomputing;

import com.ciu196.mobilecomputing.common.requests.ServerResponse;

/**
 * Created by Eric on 2017-10-18.
 */

public interface RequestDoneListener {
    void serverResponseReceived(ServerResponse response);
}
