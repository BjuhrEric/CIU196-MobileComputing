package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private static final long serialVersionUID = 540906636567484854L;
    private final ResponseValue value;
    private final ServerResponseType type;

    public ServerResponse(ServerResponseType type, ResponseValue value) {
        this.type = type;
        this.value = value;
    }

    public ResponseValue getValue() {
        return value;
    }

    public ServerResponseType getType() {
        return type;
    }



}
