package com.github.vatbub.javametricscatcher.server;

/*-
 * #%L
 * javametricscatcher.server
 * %%
 * Copyright (C) 2017 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.codahale.metrics.Counter;
import com.esotericsoftware.kryonet.Connection;
import com.github.vatbub.javametricscatcher.common.custommetrics.CustomHistogram;
import com.github.vatbub.javametricscatcher.common.custommetrics.CustomTimer;

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

    public static class ValueCollection {
        private Map<String, Counter> counterMap;
        private Map<String, CustomHistogram> histogramMap;
        private Map<String, CustomTimer> timerMap;

        public ValueCollection() {
            this(new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        public ValueCollection(Map<String, Counter> counterMap, Map<String, CustomHistogram> histogramMap, Map<String, CustomTimer> timerMap) {
            setCounter(counterMap);
            setHistogramMap(histogramMap);
            setTimerMap(timerMap);
        }

        public Map<String, Counter> getCounterMap() {
            return counterMap;
        }

        public void setCounter(Map<String, Counter> counterMap) {
            this.counterMap = counterMap;
        }

        public Map<String, CustomHistogram> getHistogramMap() {
            return histogramMap;
        }

        public void setHistogramMap(Map<String, CustomHistogram> histogramMap) {
            this.histogramMap = histogramMap;
        }

        public Map<String, CustomTimer> getTimerMap() {
            return timerMap;
        }

        public void setTimerMap(Map<String, CustomTimer> timerMap) {
            this.timerMap = timerMap;
        }
    }
}
