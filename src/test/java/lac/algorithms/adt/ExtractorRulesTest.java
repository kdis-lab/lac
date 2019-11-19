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
package lac.algorithms.adt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.ArffDataset;
import lac.data.Dataset;

public class ExtractorRulesTest extends TestSuite {
    private Dataset dataset;
    private ArrayList<Rule> expectedRules;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());

        expectedRules = new ArrayList<Rule>();
        expectedRules.add(new Rule(new Short[] { 0, 2 }, new Short((short) 4)));
        expectedRules.add(new Rule(new Short[] { 1, 3 }, new Short((short) 5)));
        expectedRules.add(new Rule(new Short[] { 0, 3 }, new Short((short) 4)));
        expectedRules.add(new Rule(new Short[] { 2 }, new Short((short) 4)));
        expectedRules.add(new Rule(new Short[] { 0 }, new Short((short) 4)));
        expectedRules.add(new Rule(new Short[] { 1 }, new Short((short) 5)));
    }

    @Test
    public void getArrayRulesAsResult() throws Exception {
        Config config = new Config();
        config.setMinConf(0.99);
        ExtractorRules algorithm = new ExtractorRules(dataset, config);

        ArrayList<Rule> rules = algorithm.run();

        assertEquals(expectedRules, rules);
    }

    @Test
    public void alteringParametersObtainDifferentNumberOfRules() {
        Config config = new Config();
        config.setMinConf(0.5);
        ExtractorRules algorithm = new ExtractorRules(dataset, config);

        ArrayList<Rule> rules = algorithm.run();

        assertEquals(9, rules.size());
    }
}
