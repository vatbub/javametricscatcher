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


import com.codahale.metrics.MetricRegistry;
import com.github.vatbub.common.core.logging.FOKLogger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

public class ConfigurationManager {
    private static final ConfigurationManager ourInstance = new ConfigurationManager();

    public static ConfigurationManager getInstance() {
        return ourInstance;
    }

    private ConfigurationManager() {
    }

    public void readConfiguration(File configFile, MetricRegistry registry) throws IOException, JDOMException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Document document = new SAXBuilder().build(configFile);

        Namespace configNameSpace = null;
        for (Namespace namespace : document.getNamespacesInScope()) {
            if (namespace.getURI().equals("https://raw.githubusercontent.com/vatbub/javametricscatcher/master/ConfigSchema.xsd"))
                configNameSpace = namespace;
        }
        for (Element reporter : document.getRootElement().getChildren("reporter", configNameSpace)) {
            String configuratorName = reporter.getAttribute("configuratorClass").getValue();
            FOKLogger.info(ConfigurationManager.class.getName(), "Configuring " + configuratorName + "...");
            Class<?> reporterConfiguratorClass = Class.forName(configuratorName);
            ((ReporterConfigurator) reporterConfiguratorClass.newInstance()).configure(registry, reporter, configNameSpace);
        }
    }

    public void getTagAndSetConfig(Element config, Namespace configNamespace, String tagToGet, SetConfigRunnable runnableToSetConfig){
        Element tag = config.getChild(tagToGet, configNamespace);
        if (tag!=null){
            runnableToSetConfig.run(tag.getValue());
        }
    }
}
