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


import com.github.vatbub.common.core.logging.FOKLogger;
import org.junit.Assert;
import org.junit.Test;

public class CustomCounterTest {
    @Test
    public void typeTest() {
        Assert.assertEquals(MetricType.COUNTER, new CustomCounter().getMetricType());
    }

    @Test
    public void serializableDataTest() {
        CustomCounter customCounter = new CustomCounter();
        for (long i = 1; i <= 10; i++) {
            FOKLogger.info(CustomCounterTest.class.getName(), "i = " + i);
            customCounter.inc();
            Assert.assertEquals(i, (long) customCounter.getSerializableData());
        }
    }

    @Test
    public void additionalMetadataTest(){
        Assert.assertNull(new CustomCounter().getAdditionalMetadata());
    }
}
