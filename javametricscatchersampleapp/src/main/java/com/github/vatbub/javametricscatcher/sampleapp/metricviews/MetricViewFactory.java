package com.github.vatbub.javametricscatcher.sampleapp.metricviews;

import com.codahale.metrics.Metric;
import com.github.vatbub.javametricscatcher.sampleapp.Main;

import java.io.IOException;

public  class MetricViewFactory {
    public static void show(Main.MetricType metricType, Metric metric) throws IOException {
        switch(metricType){
            case CustomCounter:
                CustomCounter.show((com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter) metric);
                break;
            case CustomHistogram:
                break;
            case CustomTimer:
                break;
            case IntegerGauge:
                break;
            case LongGauge:
                break;
        }
    }
}
