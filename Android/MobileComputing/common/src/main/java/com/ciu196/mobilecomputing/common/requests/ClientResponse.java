package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

/**
 * Created by Eric on 2017-10-18.
 */

public class ClientResponse implements Serializable {

    private static final long serialVersionUID = 8422513111313554801L;
    private final ClientResponseType type;
    private final ResponseValue value;

    public ClientResponse(ClientResponseType type, ResponseValue value) {
        this.type = type;
        this.value = value;
    }

    public ResponseValue getValue() {
        return value;
    }

    public ClientResponseType getType() {
        return type;
    }

}
