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
package lac.algorithms.acac;

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
        Rule rule = new Rule(new short[] { 0 }, 1L, (short) 3, 2L);
        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(rule)));
        assertEquals(rule.getKlass(), classifier.predict(instance));
    }

    @Test
    public void whenMultipleRuleAreFiredReturnKlassWithGreaterInformationGain() {
        Classifier classifier = new Classifier(new ArrayList<Rule>(Arrays.asList(
                new Rule(new short[] { 0 }, 10L, (short) 3, 10L), new Rule(new short[] { 0 }, 1L, (short) 4, 2L),
                new Rule(new short[] { 0 }, 1L, (short) 4, 2L))));
        assertEquals(4, classifier.predict(instance));
    }
}
