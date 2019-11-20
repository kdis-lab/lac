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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import lac.algorithms.IncompatibleDataset;

public class ConfigExecutionTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void dataAreCorrectlySetted() {
        ConfigExecution config = new ConfigExecution("name", null, "train", "test", new ArrayList<String>(), "report");

        assertEquals("name", config.getNameAlgorithm());
        assertEquals(null, config.getTraining());
        assertEquals(null, config.getTest());
        assertEquals(null, config.getClassifier());
        assertNotEquals(null, config.getTrainReport());
        assertNotEquals(null, config.getTestReport());
    }

    @Test(expected = IncompatibleDataset.class)
    public void whenDatasetAreIncompatibleRaiseException() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset.arff").getFile());
        ConfigExecution config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                "test", new ArrayList<String>(), "report");
        config.run();
        fail();
    }

    @Test
    public void whenTrainingIsCorrect() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        ConfigExecution config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                file.getAbsolutePath(), new ArrayList<String>(), null);
        config.run();

        assertEquals(1.0, config.getTrainReport().getAccuracy(), 0.01);
        assertTrue(config.getTrainReport().getTotalTime() >= 0.0);
        assertNotEquals(null, config.getClassifier());

        assertEquals(1.0, config.getTestReport().getAccuracy(), 0.01);
        assertTrue(config.getTestReport().getTotalTime() >= 0.0);
    }

    @Test
    public void whenReportIsGeneratedCorrectly() throws Exception {
        String folder = tempFolder.newFolder().getAbsolutePath();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        ConfigExecution config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                file.getAbsolutePath(), new ArrayList<String>(Arrays.asList("MetricsReport")),
                folder);
        config.run();
        File f = new File(folder + "/MetricsReport.training");
        assertTrue(f.exists());
        f = new File(folder + "/MetricsReport.test");
        assertTrue(f.exists());
    }

    @Test
    public void whenReportsAreGeneratedCorrectly() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        ConfigExecution config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                file.getAbsolutePath(), new ArrayList<String>(Arrays.asList("MetricsReport", "KlassReport")),
                tempFolder.getRoot().getAbsolutePath());
        config.run();
        File f = new File(tempFolder.getRoot() + "/MetricsReport.training");
        assertTrue(f.exists());
        f = new File(tempFolder.getRoot() + "/MetricsReport.test");
        assertTrue(f.exists());

        f = new File(tempFolder.getRoot() + "/KlassReport.training");
        assertTrue(f.exists());
        f = new File(tempFolder.getRoot() + "/KlassReport.test");
        assertTrue(f.exists());
    }
}
