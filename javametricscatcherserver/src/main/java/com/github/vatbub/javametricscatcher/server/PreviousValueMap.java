package com.github.vatbub.javametricscatcher.server;

import com.codahale.metrics.Counter;
import com.esotericsoftware.kryonet.Connection;

import java.util.HashMap;
import java.util.Map;

public class PreviousValueMap extends HashMap<Connection, PreviousValueMap.ValueCollection> {
    public PreviousValueMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public PreviousValueMap(int initialCapacity) {
        super(initialCapacity);
    }

    public PreviousValueMap() {
    }

    public PreviousValueMap(Map<? extends Connection, ? extends ValueCollection> m) {
        super(m);
    }

    public static class ValueCollection{
        private Map<String, Counter> counterMap;

        public ValueCollection(){
            this(new HashMap<>());
        }

        public ValueCollection(Map<String, Counter> counterMap){
            setCounter(counterMap);
        }

        public Map<String, Counter> getCounterMap() {
            return counterMap;
        }

        public void setCounter(Map<String, Counter> counterMap) {
            this.counterMap = counterMap;
        }
    }
}
