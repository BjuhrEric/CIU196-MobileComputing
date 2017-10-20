package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

public enum ServerRequestType implements Serializable {
    SEND_BROADCAST, RECEIVE_BROADCAST, RECEIVE_REACTION, CONFIRM_CONNECTIVITY
}
