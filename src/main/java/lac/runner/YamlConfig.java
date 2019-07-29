/**
 * This file is part of Library for Associative Classification (LAC)
 *
 * Copyright (C) 2019
 *   
 * LAC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. You should have 
 * received a copy of the GNU General Public License along with 
 * this program.  If not, see http://www.gnu.org/licenses/
 */
package lac.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import lac.algorithms.Config;

/**
 * Reads a YML file and returns an array with all the executions defined in this
 * file
 */
public class YamlConfig {
    /**
     * Executions specified in the configuration file
     */
    private ArrayList<ConfigExecution> executions;

    /**
     * Constructor for reading a YML config file
     * 
     * @param filename of the configuration file to be read
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public YamlConfig(String filename) throws Exception {
        this.executions = new ArrayList<ConfigExecution>();

        InputStream input = null;
        try {
            input = new FileInputStream(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Yaml yaml = new Yaml();

        Map<String, Object> obj = yaml.load(input);

        // config must include executions
        if (!obj.containsKey("executions")) {
            throw new Exception("Config invalid. it must include executions");
        }

        // Executions must be an array
        if (!(obj.get("executions") instanceof ArrayList)) {
            throw new Exception("Config invalid. executions must be an array");
        }

        ArrayList<Object> executions = (ArrayList<Object>) obj.get("executions");

        // Change from map to ConfigExecution
        for (int i = 0; i < executions.size(); i++) {
            Map<String, Object> execution = (Map<String, Object>) executions.get(i);

            // Instanciate configuration
            String nameAlgorithm = ((String) execution.get("name_algorithm")).toLowerCase();
            Config config = (Config) Class.forName("lac.algorithms." + nameAlgorithm + ".Config").newInstance();

            // For each parameter of the configuration of yaml, set in config
            Map<String, Object> configuration = (Map<String, Object>) execution.get("configuration");

            if (configuration != null && !configuration.isEmpty()) {
                for (Entry<String, Object> entry : configuration.entrySet()) {
                    String methodName = this.getSetterConfigName(entry.getKey());

                    @SuppressWarnings("rawtypes")
                    Class clazz = entry.getValue().getClass();

                    try {
                        Method setMethod = config.getClass().getMethod(methodName, clazz);

                        setMethod.invoke(config, clazz.cast(entry.getValue()));
                    } catch (NoSuchMethodException e) {
                        System.out
                                .println("Property " + entry.getKey() + " may not be compatible with " + nameAlgorithm);
                    }
                }
            }

            this.executions.add(new ConfigExecution((String) execution.get("name_algorithm"), config,
                    (String) execution.get("train"), (String) execution.get("test"),
                    (ArrayList<String>) execution.get("report_type"), (String) execution.get("report")));
        }
    }

    /**
     * Returns the executions configured for this config file
     * 
     * @return Array of the executions specified in the config file
     */
    public ArrayList<ConfigExecution> getExecutions() {
        return this.executions;
    }

    /**
     * It transforms each property of configuration from the name specified in the
     * config file using underscore, to the camelCase version
     * 
     * @param uncapitalizedName original name of the property specified in the
     *                          configuration file
     * @return the name of the setter for the specified uncapitalized name
     */
    private String getSetterConfigName(String uncapitalizedName) {
        Pattern p = Pattern.compile("_([a-zA-Z])");
        Matcher m = p.matcher(uncapitalizedName);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        String name = sb.substring(0, 1).toUpperCase() + sb.substring(1);

        return "set" + name;
    }
}
