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

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import junit.framework.TestSuite;

import lac.algorithms.Rule;
import lac.data.Attribute;
import lac.runner.ConfigExecution;

public class ClassifierReportTest extends TestSuite {
    @org.junit.Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    String outputPath;
    ClassifierReport report;
    ConfigExecution config;

    @Before
    public void setup() throws Exception {
        outputPath = tempFolder.newFile().getAbsolutePath();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        config = new ConfigExecution("CBA", new lac.algorithms.cba.Config(), file.getAbsolutePath(),
                file.getAbsolutePath(), new ArrayList<String>(), tempFolder.getRoot().getAbsolutePath());
        config.run();
        report = new ClassifierReport(config);
        report.write(outputPath);
    }

    @Test
    public void createAFileWithClassifierExtension() {
        assertTrue(new File(outputPath + ".classifier").exists());
    }

    @Test
    public void hasSameLinesAsRulesHasClassifier() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + ".classifier"));

        int lines = 0;
        while (br.readLine() != null)
            lines++;
        br.close();
        assertEquals(config.getClassifier().getNumberRules(), lines);
    }

    @Test
    public void rulesAreWrittenCorrectly() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputPath + ".classifier"));

        String line;
        int index = 0;
        while ((line = br.readLine()) != null) {
            Rule rule = config.getClassifier().getRules().get(index);
            index += 1;
            String[] antecedent = new String[rule.getAntecedent().size()];
            for (int j = 0; j < antecedent.length; j++) {
                Attribute attr = config.getTraining().getAttributeByIndex(rule.getAntecedent().get(j));
                antecedent[j] = attr.getName() + "="
                        + config.getTraining().getValueByIndex(rule.getAntecedent().get(j));
            }

            String klass = config.getTraining().getValueByIndex(rule.getKlass());
            assertEquals(String.join(" ", antecedent) + " => " + klass, line);
        }
        br.close();
    }
}
