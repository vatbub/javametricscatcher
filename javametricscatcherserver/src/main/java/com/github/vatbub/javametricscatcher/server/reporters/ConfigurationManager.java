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


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.ServiceLoader;

public class ConfigurationManager {
    private static ConfigurationManager ourInstance = new ConfigurationManager();

    public static ConfigurationManager getInstance() {
        return ourInstance;
    }

    private ConfigurationManager() {
    }

    public void readConfiguration(File configFile) throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(configFile));
        Map document = (Map) reader.read();

        ServiceLoader<ReporterConfigurator> loader = ServiceLoader.load(ReporterConfigurator.class);
        for (ReporterConfigurator implClass : loader) {

        }
    }
}
