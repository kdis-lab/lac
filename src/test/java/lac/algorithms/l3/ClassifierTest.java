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
package lac.algorithms.l3;

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
import lac.algorithms.Rule;

public class ClassifierTest extends TestSuite {
    private Instance instance;
    private Dataset dataset;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());

        instance = new Instance(3);
        instance.set(0, Short.valueOf((short) 0));
        instance.set(1, Short.valueOf((short) 1));
        instance.set(2, Short.valueOf((short) 2));
        instance.setKlass(Short.valueOf((short) 4));
    }

    @Test
    public void whenNoRuleIsFiredNoPredictionIsSaid() {
        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>());
        assertEquals(Classifier.NO_PREDICTION, classifier.predict(instance));
    }

    @Test
    public void whenOnlyOneRuleIsFiredReturnKlassForIt() {
        Rule rule = new lac.algorithms.l3.Rule(new short[] { 0 }, (short) 4);
        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>(Arrays.asList(rule)));
        assertEquals(rule.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterConfidence() {
        lac.algorithms.l3.Rule rule1 = new lac.algorithms.l3.Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 1 }, (short) 5);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(1);

        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>(Arrays.asList(rule1, rule2)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterSupport() {
        lac.algorithms.l3.Rule rule1 = new lac.algorithms.l3.Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 1 }, (short) 5);
        rule1.setSupportAntecedent(1);
        rule1.setSupportRule(1);

        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>(Arrays.asList(rule1, rule2)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithSmallerSize() {
        lac.algorithms.l3.Rule rule1 = new lac.algorithms.l3.Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 1, 2 }, (short) 5);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>(Arrays.asList(rule2, rule1)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassByLexicographyOrder() {
        lac.algorithms.l3.Rule rule1 = new lac.algorithms.l3.Rule(new short[] { 0, 1 }, (short) 4);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 1, 2 }, (short) 5);
        rule1.setSupportAntecedent(2);
        rule1.setSupportRule(2);

        Classifier classifier = new Classifier(dataset, new ArrayList<Rule>(Arrays.asList(rule2, rule1)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }
}
