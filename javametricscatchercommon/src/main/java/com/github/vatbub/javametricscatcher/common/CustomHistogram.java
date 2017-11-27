package com.github.vatbub.javametricscatcher.common;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Reservoir;

import java.util.LinkedList;
import java.util.List;

public class CustomHistogram extends Histogram implements MetricHistory<Long>{
    private List<Long> updateHistory;
    private Reservoir reservoir;

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

    public Reservoir getNewInstanceOfSameReservoirType() throws IllegalAccessException, InstantiationException {
        return getReservoir().getClass().newInstance();
    }

    public Reservoir getReservoir() {
        return reservoir;
    }
}
