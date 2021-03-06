package com.github.vatbub.javametricscatcher.server.reporters;

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


import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricRegistry;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConsoleConfigurator implements ReporterConfigurator<ConsoleReporter> {
    @Override
    public ConsoleReporter configure(MetricRegistry registry, Element xmlConfig, Namespace configNamespace) {
        ConsoleReporter.Builder builder = ConsoleReporter.forRegistry(registry);

        ConfigurationManager.getInstance().getTagAndSetConfig(xmlConfig, configNamespace, "convertDurationsTo", value -> builder.convertDurationsTo(TimeUnit.valueOf(value)));
        ConfigurationManager.getInstance().getTagAndSetConfig(xmlConfig, configNamespace, "convertRatesTo", value -> builder.convertRatesTo(TimeUnit.valueOf(value)));
        ConfigurationManager.getInstance().getTagAndSetConfig(xmlConfig, configNamespace, "shutdownExecutorOnStop", value -> builder.shutdownExecutorOnStop(Boolean.parseBoolean(value)));
        ConfigurationManager.getInstance().getTagAndSetConfig(xmlConfig, configNamespace, "formattedForLocale", value -> builder.formattedFor(Locale.forLanguageTag(value)));
        ConfigurationManager.getInstance().getTagAndSetConfig(xmlConfig, configNamespace, "formattedForTimezone", value -> builder.formattedFor(TimeZone.getTimeZone(value)));

        Element disabledMetricAttributesElement = xmlConfig.getChild("disabledMetricAttributes", configNamespace);
        if (disabledMetricAttributesElement != null) {
            List<Element> disabledMetricAttributesElements = disabledMetricAttributesElement.getChildren("metric", configNamespace);
            Set<MetricAttribute> disabledMetricAttributes = new HashSet<>();
            for (Element element : disabledMetricAttributesElements) {
                disabledMetricAttributes.add(MetricAttribute.valueOf(element.getValue()));
            }
            builder.disabledMetricAttributes(disabledMetricAttributes);
        }


        /*
        public ConsoleReporter.Builder outputTo(PrintStream output) {
            this.output = output;
            return this;
        }*/

        /*
        public ConsoleReporter.Builder filter(MetricFilter filter) {
            this.filter = filter;
            return this;
        }*/

        ConsoleReporter res = builder.build();
        res.start(1, TimeUnit.SECONDS);
        return res;
    }
}
