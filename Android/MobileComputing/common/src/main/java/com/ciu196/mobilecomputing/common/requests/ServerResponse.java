package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse implements ServerMessage {

    private static final long serialVersionUID = 540906636567484854L;
    private final ResponseValue value;
    private final ResponseType type;

    public ServerResponse(ResponseType type, ResponseValue value) {
        this.type = type;
        this.value = value;
    }

    public ResponseValue getValue() {
        return value;
    }

    public ResponseType getType() {
        return type;
    }

    public enum ResponseType {
        REQUEST_ACCEPTED, REQUEST_DECLINED, STATUS
    }

    public static class ResponseValue implements Serializable {

    }

    public static class NoValue extends ResponseValue {

        private static final long serialVersionUID = -1582436423522968513L;
    }

    public static class Status extends  ResponseValue {


        private static final long serialVersionUID = -1520252080975424543L;
        private final Map<String, String> statusMap;
        public Status() {
            statusMap = new HashMap<>();
        }

        public String getStatus(final String key) {
            return statusMap.get(key);
        }

        public void putStatus(final String key, final String value) {
            statusMap.put(key, value);
        }

    }
}
