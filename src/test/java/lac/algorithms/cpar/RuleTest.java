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
package lac.algorithms.cpar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.hamcrest.CoreMatchers;
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
    public void calculateLaplaceCorrectly() {
        Rule rule = new Rule(new short[] { 0 }, (short) 4);
        rule.calculateLaplaceAccuracy(dataset);
        assertEquals(0.75, rule.getLaplace(), 0.01);

        rule = new Rule(new short[] { 1 }, (short) 4);
        rule.calculateLaplaceAccuracy(dataset);
        assertEquals(0.33, rule.getLaplace(), 0.01);

        rule = new Rule(new short[] { 2 }, (short) 4);
        rule.calculateLaplaceAccuracy(dataset);
        assertEquals(0.66, rule.getLaplace(), 0.01);
    }

    @Test
    public void toStringIncludesLaplace() {
        Rule rule = new Rule(new short[] { 0 }, (short) 4);
        rule.calculateLaplaceAccuracy(dataset);
        assertThat(rule.toString(), CoreMatchers.containsString("Laplace: " + rule.getLaplace()));
    }
}
