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

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;

public class AprioriTest extends TestSuite {
    private Dataset dataset;
    private ArrayList<Rule> expectedRules;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());

        expectedRules = new ArrayList<Rule>();
        expectedRules.add(new Rule(new short[] { 0 }, 1, (short) 4, 2));
        expectedRules.add(new Rule(new short[] { 1 }, 1, (short) 5, 1));
        expectedRules.add(new Rule(new short[] { 2 }, 1, (short) 4, 2));
    }

    @Test
    public void getArrayRulesAsResult() throws Exception {
        Config config = new Config();
        Apriori algorithm = new Apriori(dataset, config);

        ArrayList<Rule> rules = algorithm.run();

        assertEquals(expectedRules, rules);
    }

    @Test
    public void alteringParametersObtainDifferentNumberOfRules() {
        Config config = new Config();
        config.setMinConf(0.5);
        config.setMinSup(0.2);
        Apriori algorithm = new Apriori(dataset, config);

        ArrayList<Rule> rules = algorithm.run();

        assertEquals(5, rules.size());
    }
}
