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
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Before;

import static org.hamcrest.CoreMatchers.containsString;

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
    public void evaluateCalculatesCorrectlySupports() {
        Rule rule = new Rule(new short[] { 0 }, 0L, (short) 4, 0L);
        rule.evaluate(dataset);

        assertEquals(2L, rule.getSupportRule());
        assertEquals(1.0, rule.getAllConfidence(), 0.0);
        assertEquals(2L, rule.getSupportAntecedent());
        assertEquals(2L, rule.getSupportKlass());
        assertEquals(1.0, rule.getConfidence(), 0.0);
    }

    @Test
    public void ruleContainsAllConfWhenPrinted() {
        Rule rule = new Rule(new short[] { 0 }, 1L, (short) 0, 2L);
        assertThat(rule.toString(), containsString("AllConf: 1.0"));
    }

    @Test
    public void getAllConfidenceWithSize1Returns1() {
        Rule rule = new Rule(new short[] { 0 }, 1L, (short) 0, 2L);
        assertEquals(1.0, rule.getAllConfidence(), 0.0);
    }

    @Test
    public void getAllConfidenceWithNegativeSupportReturnsNan() {
        Rule rule = new Rule(new short[] { 0, 1 }, 1L, (short) 0, 2L);
        rule.setMaximums(-1L, -2L);
        assertEquals(Double.NaN, rule.getAllConfidence(), 0.0);
    }

    @Test
    public void getAllConfidenceWithCalculatedValue() {
        Rule rule = new Rule(new short[] { 0, 1 }, 1L, (short) 0, 10L);
        rule.setMaximums(5L, 2L);
        assertEquals(10 / 5.0, rule.getAllConfidence(), 0.0);
    }
}
