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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import junit.framework.TestSuite;

import lac.data.Instance;
import lac.metrics.ConfusionMatrix;
import lac.runner.ConfigExecution;

public class MetricsReportTest extends TestSuite {
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
                file.getAbsolutePath(), new ArrayList<String>(Arrays.asList("MetricsReport")),
                tempFolder.getRoot().getAbsolutePath());
        config.run();
        report = new ClassifierReport(config);
        report.write(outputPath);
    }

    @Test
    public void createsTwoFiles() {
        assertTrue(new File(outputPath + "/MetricsReport.training").exists());
        assertTrue(new File(outputPath + "/MetricsReport.test").exists());
    }

    @Test
    public void writesRealAndPredictedWithHeaderForTraining() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/MetricsReport.training"));

        ConfusionMatrix matrix = new ConfusionMatrix();
        for (int i = 0; i < config.getTraining().size(); i++) {
            Instance example = config.getTraining().getInstance(i);
            short predictedKlass = config.getClassifier().predict(example);
            short realKlass = config.getTraining().getKlassInstance(i);

            matrix.add(realKlass, predictedKlass);
        }

        assertEquals("Name metric, Value", br.readLine());
        assertEquals("Accuracy, " + matrix.getAccuracy(), br.readLine());
        assertEquals("Kappa, " + matrix.getKappa(), br.readLine());
        assertEquals("Recall, " + matrix.getAverageRecall(), br.readLine());
        assertEquals("Precision, " + matrix.getAveragePrecision(), br.readLine());
        assertEquals("F-measure(micro), " + matrix.getMicroFMeasure(), br.readLine());
        assertEquals("F-measure(macro), " + matrix.getMacroFMeasure(), br.readLine());
        assertEquals("Number rules, " + config.getClassifier().getNumberRules(), br.readLine());
        assertThat(br.readLine(), CoreMatchers.containsString("Average number attributes,"));
        assertThat(br.readLine(), CoreMatchers.containsString("Runtime training (ms)"));
        assertThat(br.readLine(), CoreMatchers.containsString("Runtime test (ms)"));

        br.close();
    }

    @Test
    public void writesRealAndPredictedWithHeaderForTest() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + "/MetricsReport.test"));

        ConfusionMatrix matrix = new ConfusionMatrix();
        for (int i = 0; i < config.getTest().size(); i++) {
            Instance example = config.getTest().getInstance(i);
            short predictedKlass = config.getClassifier().predict(example);
            short realKlass = config.getTest().getKlassInstance(i);

            matrix.add(realKlass, predictedKlass);
        }

        assertEquals("Name metric, Value", br.readLine());
        assertEquals("Accuracy, " + matrix.getAccuracy(), br.readLine());
        assertEquals("Kappa, " + matrix.getKappa(), br.readLine());
        assertEquals("Recall, " + matrix.getAverageRecall(), br.readLine());
        assertEquals("Precision, " + matrix.getAveragePrecision(), br.readLine());
        assertEquals("F-measure(micro), " + matrix.getMicroFMeasure(), br.readLine());
        assertEquals("F-measure(macro), " + matrix.getMacroFMeasure(), br.readLine());
        assertEquals("Number rules, " + config.getClassifier().getNumberRules(), br.readLine());
        assertThat(br.readLine(), CoreMatchers.containsString("Average number attributes,"));
        assertThat(br.readLine(), CoreMatchers.containsString("Runtime training (ms)"));
        assertThat(br.readLine(), CoreMatchers.containsString("Runtime test (ms)"));

        br.close();
    }
}
