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
package lac.algorithms.acn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;
import lac.data.Instance;

public class ClassifierTest extends TestSuite {
    private Instance instance;
    private Dataset dataset;

    @Before
    public void setup() throws Exception {
        instance = new Instance(2);
        instance.set(0, Short.valueOf((short) 0));
        instance.set(1, Short.valueOf((short) 1));
        instance.setKlass(Short.valueOf((short) 3));

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());
    }

    @Test
    public void usesMajorityKlassWithoutRules() {
        Classifier classifier = new Classifier(new ArrayList<Rule>(), dataset, new Config());
        assertEquals(4, classifier.predict(instance));
    }

    @Test
    public void whenOnlyOneRuleIsFiredReturnKlassForIt() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);
        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule)), dataset, new Config());
        assertEquals(rule.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterConfidence()
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        Rule rule2 = newRule(new short[] { 1 }, (short) 4, 0.98, 5L, 4L);
        Rule rule1 = newRule(new short[] { 0 }, (short) 3, 0.98, 5L, 5L);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)), dataset, new Config());
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterPearson()
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        Rule rule2 = newRule(new short[] { 1 }, (short) 4, 0.97, 5L, 5L);
        Rule rule1 = newRule(new short[] { 0 }, (short) 3, 0.99, 5L, 5L);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)), dataset, new Config());
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterSupportRule()
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        Rule rule2 = newRule(new short[] { 1 }, (short) 4, 0.97, 4L, 4L);
        Rule rule1 = newRule(new short[] { 0 }, (short) 3, 0.97, 5L, 5L);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)), dataset, new Config());
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithNegativeItems()
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        Config config = new Config();
        config.setMinAcc(0.0);
        Rule rule2 = newRule(new short[] { 0, 2 }, (short) 5, 0.97, 5L, 5L);
        rule2.negateItem(0);
        rule2.negateItem(1);
        Rule rule1 = newRule(new short[] { 1, 2 }, (short) 4, 0.97, 5L, 5L);
        rule1.negateItem(0);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)), dataset, config);
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    private Rule newRule(short[] antecedent, short klass, Double pearson, Long supportAntecedent, Long supportRule)
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        Rule rule = new Rule(klass);
        rule.add(antecedent);
        FieldSetter.setField(rule, rule.getClass().getDeclaredField("pearson"), pearson);
        FieldSetter.setField(rule, Class.forName("lac.algorithms.Rule").getDeclaredField("supportAntecedent"),
                supportAntecedent);
        FieldSetter.setField(rule, Class.forName("lac.algorithms.Rule").getDeclaredField("supportRule"), supportRule);

        return rule;
    }
}
