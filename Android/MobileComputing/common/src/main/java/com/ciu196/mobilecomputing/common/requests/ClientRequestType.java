package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

public enum ClientRequestType implements Serializable {
    BROADCAST, LISTEN, STOP_BROADCAST, STOP_LISTEN, DETACH_CLIENT, SEND_DATA, REQUEST_DATA, REQUEST_STATUS, SEND_REACTION, IDENTIFY
}
