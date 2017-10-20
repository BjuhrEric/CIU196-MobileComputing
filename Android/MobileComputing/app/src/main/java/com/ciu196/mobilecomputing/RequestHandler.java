package com.ciu196.mobilecomputing;

import com.ciu196.mobilecomputing.common.requests.ClientRequest;
import com.ciu196.mobilecomputing.common.requests.ServerResponse;

/**
 * Created by Eric on 2017-10-18.
 */

public interface RequestHandler {

    ServerResponse handleRequest(ClientRequest request);

}
