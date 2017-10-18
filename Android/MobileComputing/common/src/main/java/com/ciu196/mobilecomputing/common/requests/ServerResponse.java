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
        REQUEST_ACCEPTED, REQUEST_DECLINED, STATUS, DETACHED
    }

    public interface ResponseValue<T extends Serializable> extends Serializable {
        T getValue();
    }

    public static class NoValue implements ResponseValue {

        private static final long serialVersionUID = -1582436423522968513L;

        @Override
        public Serializable getValue() {
            return null;
        }
    }

    public static class Status implements ResponseValue<HashMap<String, String>> {

        private static final long serialVersionUID = -1520252080975424543L;
        private final HashMap<String, String> statusMap;
        public Status() {
            statusMap = new HashMap<>();
        }

        public String getStatus(final String key) {
            return statusMap.get(key);
        }

        public void putStatus(final String key, final String value) {
            statusMap.put(key, value);
        }

        @Override
        public HashMap<String, String> getValue() {
            return statusMap;
        }
    }

    public static class SingleObjectValue<T extends Serializable> implements ResponseValue<T> {

        private final T value;

        public SingleObjectValue(T value) {
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }
}
