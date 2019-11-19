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
package lac.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.Instance;

public class ClassifierTest extends TestSuite {
    private Classifier classifier;

    private Instance example = new Instance(2);

    Rule matchingRule = new Rule(new short[] { 1, 2 }, (short) 3);
    Rule matchingRule2 = new Rule(new short[] { 1, 2 }, (short) 5);
    Rule notMatchingRule = new Rule(new short[] { 2, 0 }, (short) 4);

    @Before
    public void setup() {
        example.set(0, new Short((short) 1));
        example.set(1, new Short((short) 2));
        classifier = new Classifier();
    }

    @Test
    public void byDefaultRulesAreEmpty() {
        assertEquals(classifier.getNumberRules(), 0);
        assertTrue(classifier.getRules().isEmpty());
    }

    @Test
    public void addRulesIncreasesNumberRules() {
        classifier.add(new Rule());
        assertEquals(classifier.getNumberRules(), 1);
        assertFalse(classifier.getRules().isEmpty());
    }

    @Test
    public void predictingWhenMultipleRulesMatchReturnFirst() {
        classifier.add(notMatchingRule);
        classifier.add(matchingRule);
        classifier.add(matchingRule2);
        assertEquals(matchingRule.getKlass(), classifier.predict(example));
    }

    @Test
    public void predictingWhenNoRuleMatchsReturnNoPrediction() {
        classifier.add(notMatchingRule);
        assertEquals(Classifier.NO_PREDICTION, classifier.predict(example));
    }
}
