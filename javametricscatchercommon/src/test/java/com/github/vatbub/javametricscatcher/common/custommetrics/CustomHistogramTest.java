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
import org.junit.Assert;
import org.junit.Test;

public class CustomHistogramTest {
    @Test
    public void reservoirTest() {
        Assert.assertTrue(new CustomHistogram().getReservoir() instanceof ExponentiallyDecayingReservoir);
    }

    @Test
    public void typeTest() {
        Assert.assertEquals(MetricType.HISTOGRAM, new CustomHistogram().getMetricType());
    }

    @Test
    public void serializableDataTest() {
        CustomHistogram histogram = new CustomHistogram();
        long[] values = new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (long value : values) {
            histogram.update(value);
        }

        Assert.assertTrue(histogram.getUpdateHistory() == histogram.getSerializableData());

        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals(values[i], (long) histogram.getUpdateHistory().get(i));
        }
    }

    @Test
    public void additionalMetadataTest() {
        CustomHistogram histogram = new CustomHistogram();
        Assert.assertNotNull(histogram.getAdditionalMetadata());
        Assert.assertEquals(ExponentiallyDecayingReservoir.class.getName(), histogram.getAdditionalMetadata().get(CustomHistogram.HISTOGRAM_RESERVOIR_TYPE_PARAM_KEY));
    }
}
