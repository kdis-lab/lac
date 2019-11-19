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
package lac.algorithms.accf;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.Instance;

public class ClassifierTest extends TestSuite {
    Instance instance;

    @Before
    public void setup() {
        instance = new Instance(2);
        instance.set(0, Short.valueOf((short) 0));
        instance.set(1, Short.valueOf((short) 1));
        instance.setKlass(Short.valueOf((short) 3));
    }

    @Test
    public void whenNoRuleIsFiredNoPredictionIsSaid() {
        Classifier classifier = new Classifier(new ArrayList<Rule>());
        assertEquals(Classifier.NO_PREDICTION, classifier.predict(instance));
    }

    @Test
    public void whenOnlyOneRuleIsFiredReturnKlassForIt() {
        Rule rule = new Rule(new short[] { 0 }, (short) 3);
        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule)));
        assertEquals(rule.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRulesAreFiredReturnFirstByConfidence() {
        Rule rule1 = new Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(3L);
        ;
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 0 }, (short) 5);
        rule2.setSupportAntecedent(3L);
        ;
        rule2.setSupportKlass(1);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRulesAreFiredReturnFirstBySupport() {
        Rule rule1 = new Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(2L);
        ;
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 0 }, (short) 5);
        rule2.setSupportAntecedent(1L);
        ;
        rule2.setSupportKlass(1);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRulesAreFiredReturnFirstBySize() {
        Rule rule1 = new Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportAntecedent(2L);
        ;
        rule1.setSupportRule(2);

        Rule rule2 = new Rule(new short[] { 0, 3 }, (short) 5);
        rule2.setSupportAntecedent(2L);
        ;
        rule2.setSupportKlass(2);

        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule1, rule2)));
        assertEquals(rule1.getKlass(), classifier.predict(instance));
    }
}
