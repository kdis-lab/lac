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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;

import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;

public class RuleTest extends TestSuite {
    private Dataset dataset;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());
    }

    @Test
    public void evaluateWithPositiveCalculatesCorrectlySupports() {
        Rule rule = new Rule((short) 4);
        rule.add((short) 0);
        rule.evaluate(dataset);

        assertEquals(2L, rule.getSupportRule());
        assertEquals(0.99, rule.getPearson(), 0.01);
        assertEquals(2L, rule.getSupportAntecedent());
        assertEquals(2L, rule.getSupportKlass());
        assertEquals(1.0, rule.getConfidence(), 0.0);
    }

    @Test
    public void evaluateWithNegativeCalculatesCorrectlySupports() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);
        rule.negateItem(0);
        rule.evaluate(dataset);

        assertEquals(1L, rule.getSupportRule());
        assertEquals(0.99, rule.getPearson(), 0.01);
        assertEquals(1L, rule.getSupportAntecedent());
        assertEquals(1L, rule.getSupportKlass());
        assertEquals(1.0, rule.getConfidence(), 0.0);
    }

    @Test
    public void isNegativeReturnsTrueWithNegative() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);
        rule.negateItem(0);

        assertTrue(rule.isNegative());
    }

    @Test
    public void isNegativeReturnsFalseWithPositive() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);

        assertFalse(rule.isNegative());
    }

    @Test
    public void getNegativeItemsReturns0WithPositiveRule() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);

        assertEquals(0, rule.getNegativeItems());
    }

    @Test
    public void getNegativeItemsReturnNumberOfNegative() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);
        rule.add((short) 1);
        rule.add((short) 2);
        rule.negateItem(0);
        rule.negateItem(2);

        assertEquals(2, rule.getNegativeItems());
    }

    @Test
    public void cloneReturnsNewRule() {
        Rule rule = new Rule((short) 5);
        rule.add((short) 0);
        rule.negateItem(0);
        rule.evaluate(dataset);

        assertNotSame(rule, rule.clone());
        assertEquals(rule.getNegativeItems(), ((Rule) rule.clone()).getNegativeItems());
        assertEquals(1L, rule.getSupportRule());
        assertEquals(0.99, rule.getPearson(), 0.01);
        assertEquals(1L, rule.getSupportAntecedent());
        assertEquals(1L, rule.getSupportKlass());
        assertEquals(1.0, rule.getConfidence(), 0.0);
    }

}
