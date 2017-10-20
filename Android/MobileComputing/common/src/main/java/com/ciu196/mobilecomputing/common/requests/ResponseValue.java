package com.ciu196.mobilecomputing.common.requests;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Eric on 2017-10-18.
 */

public interface ResponseValue<T extends Serializable> extends Serializable {

    T getValue();

    class NoValue implements ResponseValue {

        private static final long serialVersionUID = -1582436423522968513L;

        @Override
        public Serializable getValue() {
            return null;
        }
    }

    class SingleObjectValue<T extends Serializable> implements ResponseValue<T> {

        private static final long serialVersionUID = -4292349638680803106L;
        private final T value;

        public SingleObjectValue(T value) {
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    class Status implements ResponseValue<HashMap<String, String>> {

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
}
