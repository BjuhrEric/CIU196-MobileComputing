package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;

/**
 * Created by Eric on 2017-10-18.
 */

public class ServerRequest implements Serializable {


    private static final long serialVersionUID = 3667483401591819530L;
    private final ServerRequestType type;
    private final String msg;

    public ServerRequest(final ServerRequestType type) { this(type, null); }

    public ServerRequest(final ServerRequestType type, final String msg) {
        this.type = type;
        this.msg = msg;
    }

    public ServerRequestType getType() {
        return type;
    }

    public String getValue() { return msg; }

}
