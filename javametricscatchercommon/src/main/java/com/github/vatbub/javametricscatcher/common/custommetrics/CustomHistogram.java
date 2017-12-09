package com.github.vatbub.javametricscatcher.common.custommetrics;

/*-
 * #%L
 * javametricscatcher.common
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


import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Reservoir;
import com.github.vatbub.javametricscatcher.common.MetricHistory;
import com.github.vatbub.javametricscatcher.common.SerializableMetric;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomHistogram extends Histogram implements MetricHistory<Long>, SerializableMetric<LinkedList<Long>> {
    public static final String HISTOGRAM_RESERVOIR_TYPE_PARAM_KEY = "reservoirType";
    private final LinkedList<Long> updateHistory;
    private final Reservoir reservoir;

    public CustomHistogram() {
        this(new ExponentiallyDecayingReservoir());
    }

    /**
     * Creates a new {@link Histogram} with the given reservoir.
     *
     * @param reservoir the reservoir to create a histogram from
     */
    public CustomHistogram(Reservoir reservoir) {
        super(reservoir);
        this.reservoir = reservoir;
        updateHistory = new LinkedList<>();
    }

    @Override
    public void update(long value) {
        super.update(value);
        updateHistory.add(value);
    }

    public List<Long> getUpdateHistory() {
        return updateHistory;
    }

    public Reservoir getReservoir() {
        return reservoir;
    }

    @Override
    public LinkedList<Long> getSerializableData() {
        return updateHistory;
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.HISTOGRAM;
    }

    @Override
    public HashMap<String, String> getAdditionalMetadata() {
        HashMap<String, String> params = new HashMap<>();
        params.put(HISTOGRAM_RESERVOIR_TYPE_PARAM_KEY, getReservoir().getClass().getName());
        return params;
    }
}
