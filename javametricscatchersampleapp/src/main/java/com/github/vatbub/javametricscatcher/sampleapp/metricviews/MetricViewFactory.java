package com.github.vatbub.javametricscatcher.sampleapp.metricviews;

/*-
 * #%L
 * javametricscatcher.sampleapp
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
