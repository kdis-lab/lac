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
package lac.algorithms.cmar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;
import lac.data.Instance;

public class ClassifierTest extends TestSuite {
    private Dataset dataset;
    private Instance instance;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());

        instance = new Instance(2);
        instance.set(0, Short.valueOf((short) 0));
        instance.set(1, Short.valueOf((short) 1));
        instance.setKlass(Short.valueOf((short) 3));
    }

    @Test
    public void rulesArePrunnedUsingCover() throws Exception {
        ArrayList<lac.algorithms.Rule> originalRules = new ArrayList<lac.algorithms.Rule>();
        ArrayList<Rule> expectedRules = new ArrayList<Rule>();

        Rule rule1 = new Rule(new short[] { 0 }, (short) 4);
        originalRules.add(rule1);
        expectedRules.add(rule1);
        Rule rule2 = new Rule(new short[] { 1 }, (short) 5);
        originalRules.add(rule2);
        expectedRules.add(rule2);
        Rule rule3 = new Rule(new short[] { 2 }, (short) 4);
        originalRules.add(rule3);
        expectedRules.add(rule3);
        Rule rule4 = new Rule(new short[] { 3 }, (short) 4);
        originalRules.add(rule4);
        expectedRules.add(rule4);
        Rule rule5 = new Rule(new short[] { 3 }, (short) 5);
        originalRules.add(rule5);
        expectedRules.add(rule5);
        Rule rule6 = new Rule(new short[] { 0, 2 }, (short) 4);
        originalRules.add(rule6);
        expectedRules.add(rule6);
        Rule rule7 = new Rule(new short[] { 0, 3 }, (short) 4);
        originalRules.add(rule7);
        expectedRules.add(rule7);
        Rule rule8 = new Rule(new short[] { 1, 3 }, (short) 5);
        rule8.add(new short[] { 1, 3 });
        originalRules.add(rule8);
        expectedRules.add(rule8);

        Rule rule9 = new Rule(new short[] {}, (short) 5);
        originalRules.add(rule9);

        assertEquals(expectedRules, new Classifier(originalRules, dataset, new Config()).getRules());
    }

    @Test
    public void whenNoRuleIsFiredReturnNoPrediction() throws Exception {
        lac.algorithms.Rule rule1 = new Rule(new short[] { 8 }, (short) 5);
        Classifier classifier = new Classifier(new ArrayList<lac.algorithms.Rule>(Arrays.asList(rule1)), dataset,
                new Config());
        assertEquals(Classifier.NO_PREDICTION, classifier.predict(instance));
    }

    @Test
    public void whenOnlyOneRuleIsFiredReturnThisKlass() throws Exception {
        lac.algorithms.Rule rule1 = new Rule(new short[] { 0 }, (short) 5);
        lac.algorithms.Rule rule2 = new Rule(new short[] { 8 }, (short) 4);
        Classifier classifier = new Classifier(new ArrayList<lac.algorithms.Rule>(Arrays.asList(rule1, rule2)), dataset,
                new Config());
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenManyRulesAreFiredReturnKlassWithGreaterChiSquare() throws Exception {
        lac.algorithms.Rule rule1 = new Rule(new short[] { 0 }, (short) 5);
        lac.algorithms.Rule rule2 = new Rule(new short[] { 1 }, (short) 5);
        lac.algorithms.Rule rule3 = new Rule(new short[] { 2 }, (short) 4);
        lac.algorithms.Rule rule4 = new Rule(new short[] { 3 }, (short) 4);
        Classifier classifier = new Classifier(
                new ArrayList<lac.algorithms.Rule>(Arrays.asList(rule1, rule2, rule3, rule4)), dataset, new Config());
        assertEquals(rule3.getKlass(), classifier.predict(instance));
    }

}
