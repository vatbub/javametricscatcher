package com.github.vatbub.javametricscatcher.common;

import com.codahale.metrics.Clock;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Timer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CustomTimer extends Timer implements MetricHistory<CustomTimer.TimerEntry> {
    private List<TimerEntry> updateHistory;
    private Clock clock;
    private Reservoir reservoir;

    public CustomTimer() {
        this(new ExponentiallyDecayingReservoir());
    }

    public CustomTimer(Reservoir reservoir) {
        this(reservoir, Clock.defaultClock());
    }

    public CustomTimer(Reservoir reservoir, Clock clock) {
        super(reservoir, clock);
        this.clock = clock;
        this.reservoir = reservoir;
        updateHistory = new LinkedList<>();
    }

    @Override
    public void update(long duration, TimeUnit unit) {
        super.update(duration, unit);
        updateHistory.add(new TimerEntry(unit, duration));
    }

    @Override
    public <T> T time(Callable<T> event) throws Exception {
        final long startTime = clock.getTick();
        try {
            return super.time(event);
        } finally {
            updateHistory.add(new TimerEntry(clock.getTick() - startTime));
        }
    }

    @Override
    public void time(Runnable event) {
        final long startTime = clock.getTick();
        try {
            super.time(event);
        } finally {
            updateHistory.add(new TimerEntry(clock.getTick() - startTime));
        }
    }

    @Override
    public List<TimerEntry> getUpdateHistory() {
        return updateHistory;
    }

    public Reservoir getNewInstanceOfSameReservoirType() throws IllegalAccessException, InstantiationException {
        return getReservoir().getClass().newInstance();
    }

    public Clock getClock() {
        return clock;
    }

    public Reservoir getReservoir() {
        return reservoir;
    }

    public static class TimerEntry {
        private TimeUnit timeUnit;
        private long duration;

        /**
         * Default constructor for KryoNet
         */
        @SuppressWarnings("unused")
        public TimerEntry() {
            this(0);
        }

        public TimerEntry(long nanos) {
            this(TimeUnit.NANOSECONDS, nanos);
        }

        public TimerEntry(TimeUnit timeUnit, long duration) {
            setTimeUnit(timeUnit);
            setDuration(duration);
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TimerEntry))
                return false;

            TimerEntry that = (TimerEntry) obj;
            return getTimeUnit().toNanos(getDuration()) == that.getTimeUnit().toNanos(that.getDuration());
        }
    }
}
