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

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;

public class CRTreeTest extends TestSuite {
    private Dataset dataset;
    private CRTree tree;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());
        Rule.NUMBER_INSTANCES = dataset.size();

        tree = new CRTree(dataset, new Config());
    }

    @Test
    public void insertIngoreChiSquareByThreshold() {
        Rule rule1 = new Rule(new short[] { 0 }, (short) 4);
        rule1.setSupportRule(2);
        rule1.setSupportAntecedent(2);
        rule1.setSupportKlass(2);

        Rule rule2 = new Rule(new short[] { 0 }, (short) 4);
        rule2.setSupportRule(0);
        rule2.setSupportAntecedent(1);
        rule2.setSupportKlass(1);

        tree.insert(rule1);
        assertEquals(1, tree.getRules().size());

        tree.insert(rule2);
        assertEquals(1, tree.getRules().size());
    }

    @Test
    public void insertIngoreMoreGeneralRule() {
        Rule rule1 = new Rule(new short[] { 0, 1 }, (short) 4);
        rule1.setSupportRule(2);
        rule1.setSupportAntecedent(2);
        rule1.setSupportKlass(2);

        Rule rule2 = new Rule(new short[] { 0 }, (short) 4);
        rule2.setSupportRule(2);
        rule2.setSupportAntecedent(2);
        rule2.setSupportKlass(2);

        tree.insert(rule1);
        assertEquals(1, tree.getRules().size());

        tree.insert(rule2);
        assertEquals(1, tree.getRules().size());
    }

    @Test
    public void insertIncreaseRules() {
        Rule rule1 = new Rule(new short[] { 0, 1 }, (short) 4);
        rule1.setSupportRule(2);
        rule1.setSupportAntecedent(2);
        rule1.setSupportKlass(2);

        Rule rule2 = new Rule(new short[] { 0, 2 }, (short) 4);
        rule2.setSupportRule(2);
        rule2.setSupportAntecedent(2);
        rule2.setSupportKlass(2);

        tree.insert(rule1);
        assertEquals(1, tree.getRules().size());

        tree.insert(rule2);
        assertEquals(2, tree.getRules().size());
    }
}
