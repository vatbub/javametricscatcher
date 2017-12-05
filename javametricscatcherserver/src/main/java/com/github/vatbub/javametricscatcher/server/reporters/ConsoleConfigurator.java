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
import com.codahale.metrics.MetricRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsoleConfigurator implements ReporterConfigurator<ConsoleReporter> {
    @Override
    public ConsoleReporter configure(MetricRegistry registry, Map<String, Object> yamlConfig) {
        return ConsoleReporter.forRegistry(registry).build();
    }

    @Override
    public List<String> getAliases() {
        List<String> res = new ArrayList<>(3);
        res.add("ConsoleReporter");
        res.add("consoleReporter");
        res.add("consolereporter");
        return res;
    }
}
