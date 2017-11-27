package com.github.vatbub.javametricscatcher.common;

import java.util.List;

public interface MetricHistory<T> {
    List<T> getUpdateHistory();
}
