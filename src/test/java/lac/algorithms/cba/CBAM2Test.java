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
package lac.algorithms.cba;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;

public class CBAM2Test extends TestSuite {
    private Dataset dataset;
    private ArrayList<Rule> originalRules;
    private ArrayList<Rule> expectedRules;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());

        originalRules = new ArrayList<Rule>();
        expectedRules = new ArrayList<Rule>();

        Rule rule1 = new Rule((short) 4);
        rule1.add((short) 0);
        originalRules.add(rule1);
        expectedRules.add(rule1);
        Rule rule2 = new Rule((short) 5);
        rule2.add((short) 1);
        originalRules.add(rule2);
        Rule rule3 = new Rule((short) 4);
        rule3.add((short) 2);
        originalRules.add(rule3);
        Rule rule4 = new Rule((short) 4);
        rule4.add((short) 3);
        originalRules.add(rule4);
        Rule rule5 = new Rule((short) 5);
        rule5.add((short) 3);
        originalRules.add(rule5);
        Rule rule6 = new Rule((short) 4);
        rule6.add(new short[] { 0, 2 });
        originalRules.add(rule6);
        Rule rule7 = new Rule((short) 4);
        rule7.add(new short[] { 0, 3 });
        originalRules.add(rule7);
        Rule rule8 = new Rule((short) 5);
        rule8.add(new short[] { 1, 3 });
        originalRules.add(rule8);

        Rule rule9 = new Rule((short) 5);
        expectedRules.add(rule9);
    }

    @Test
    public void sortAndRanksRuleAccordingly() throws CloneNotSupportedException {
        CBAM2 cbam2 = new CBAM2(dataset, originalRules);
        assertEquals(expectedRules, cbam2.getClassifier().getRules());
    }
}
