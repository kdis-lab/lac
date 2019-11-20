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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = org.yaml.snakeyaml.error.YAMLException.class)
    public void readNotExistingFile() throws Exception {
        new YamlConfig("not_existing_file.yml");
    }

    @Test
    public void whenYmlDoesnotHaveExecutionsRaiseException() throws Exception {
        try {
            File tempFile = tempFolder.newFile();
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("whatever", "whatever");

            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(tempFile);
            yaml.dump(data, writer);

            new YamlConfig(tempFile.getAbsolutePath());
            fail();
        } catch (Exception e) {
            assertEquals("Config invalid. it must include executions", e.getMessage());
        }
    }

    @Test
    public void whenYmlIsNotArrayList() throws Exception {
        try {
            File tempFile = tempFolder.newFile();
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("executions", "whatever");

            Yaml yaml = new Yaml();

            FileWriter writer = new FileWriter(tempFile);
            yaml.dump(data, writer);

            new YamlConfig(tempFile.getAbsolutePath());
            fail();
        } catch (Exception e) {
            assertEquals("Config invalid. executions must be an array", e.getMessage());
        }
    }

    @Test
    public void whenYmlIsValidConfigureExecutionsCorrectly() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("config.yml").getFile());
        YamlConfig config = new YamlConfig(file.getAbsolutePath());

        assertEquals(config.getExecutions().size(), 2);
        assertEquals(config.getExecutions().get(0).getNameAlgorithm(), "MAC");
        assertEquals(config.getExecutions().get(1).getNameAlgorithm(), "ACCF");
    }
}
