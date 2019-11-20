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
package lac.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import junit.framework.TestSuite;

import lac.data.Instance;
import lac.runner.ConfigExecution;

public class KlassReportTest extends TestSuite {
    @org.junit.Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    String outputPath;
    ClassifierReport report;
    ConfigExecution config;

    @Before
    public void setup() throws Exception {
        outputPath = tempFolder.getRoot().getAbsolutePath();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                file.getAbsolutePath(), new ArrayList<String>(Arrays.asList("KlassReport")),
                tempFolder.getRoot().getAbsolutePath());
        config.run();
        report = new ClassifierReport(config);
        report.write(outputPath);
    }

    @Test
    public void createsTwoFiles() {
        assertTrue(new File(outputPath + "/KlassReport.training").exists());
        assertTrue(new File(outputPath + "/KlassReport.test").exists());
    }

    @Test
    public void trainingHasOneLineMoreThanDataset() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/KlassReport.training"));

        int lines = 0;
        while (br.readLine() != null)
            lines++;
        br.close();
        // +1 because the header line
        assertEquals(config.getTraining().size() + 1, lines);
    }

    @Test
    public void testHasOneLineMoreThanDataset() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/KlassReport.test"));

        int lines = 0;
        while (br.readLine() != null)
            lines++;
        br.close();
        // +1 because the header line
        assertEquals(config.getTest().size() + 1, lines);
    }

    @Test
    public void writesRealAndPredictedWithHeaderForTraining() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/KlassReport.training"));

        String line = br.readLine();

        // Header of the file
        assertEquals("realKlass, predictedKlass", line);

        int index = 0;
        while ((line = br.readLine()) != null) {
            Instance example = config.getTraining().getInstance(index);
            short predictedKlassIndex = config.getClassifier().predict(example);
            short realKlassIndex = config.getTraining().getKlassInstance(index);

            String realKlass = config.getTraining().getValueByIndex(realKlassIndex);
            String predictedKlass = config.getTraining().getValueByIndex(predictedKlassIndex);
            assertEquals(realKlass + "," + predictedKlass, line);
            index += 1;
        }
        br.close();
    }

    @Test
    public void writesRealAndPredictedWithHeaderForTest() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/KlassReport.test"));

        String line = br.readLine();

        // Header of the file
        assertEquals("realKlass, predictedKlass", line);

        int index = 0;
        while ((line = br.readLine()) != null) {
            Instance example = config.getTest().getInstance(index);
            short predictedKlassIndex = config.getClassifier().predict(example);
            short realKlassIndex = config.getTest().getKlassInstance(index);

            String realKlass = config.getTest().getValueByIndex(realKlassIndex);
            String predictedKlass = config.getTest().getValueByIndex(predictedKlassIndex);
            assertEquals(realKlass + "," + predictedKlass, line);
            index += 1;
        }
        br.close();
    }
}
