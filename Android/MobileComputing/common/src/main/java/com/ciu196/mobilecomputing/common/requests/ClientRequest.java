package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

public final class ClientRequest implements Serializable {

    private static final long serialVersionUID = -6781481050664522565L;
    private final String val;
    private final ClientRequestType type;

    public ClientRequest(ClientRequestType type) {
        this(type, null);
    }

    public ClientRequest(ClientRequestType type, String val) {
        this.type = type;
        this.val = val;
    }

    public String getValue() {
        return val;
    }

    public ClientRequestType getType() {
        return type;
    }


}
