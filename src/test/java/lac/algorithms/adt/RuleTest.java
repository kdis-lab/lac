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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {

    @Test
    public void incrementSupportRuleIncrementBy1() {
        Rule rule = new Rule((short) 3);
        rule.incrementSupportRule();
        assertEquals(1L, rule.getSupportRule());
        rule.incrementSupportRule();
        assertEquals(2L, rule.getSupportRule());
        rule.incrementSupportRule();
        assertEquals(3L, rule.getSupportRule());
    }

    @Test
    public void incrementSupportAntecedentIncrementBy1() {
        Rule rule = new Rule((short) 3);
        rule.incrementSupportAntecedent();
        assertEquals(1L, rule.getSupportAntecedent());
        rule.incrementSupportAntecedent();
        assertEquals(2L, rule.getSupportAntecedent());
        rule.incrementSupportAntecedent();
        assertEquals(3L, rule.getSupportAntecedent());
    }

    @Test
    public void incrementMissesIncrementBy1() {
        Rule rule = new Rule((short) 3);
        rule.incrementMisses();
        assertEquals(1.0, rule.getMisses(), 0.0);
        rule.incrementMisses();
        assertEquals(2.0, rule.getMisses(), 0.0);
        rule.incrementMisses();
        assertEquals(3.0, rule.getMisses(), 0.0);
        rule.incrementMisses();
    }

    @Test
    public void addCoveredInstances() {
        Rule rule = new Rule((short) 3);
        rule.addCoveredInstance(1);
        assertEquals(new ArrayList<Integer>(Arrays.asList(1)), rule.getCoveredInstances());
        rule.addCoveredInstance(3);
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 3)), rule.getCoveredInstances());
    }

    @Test
    public void cloneReturnsNewInstance() {
        Rule rule = new Rule((short) 3);
        rule.add((short) 2);
        Rule newRule = new Rule((short) 3);
        newRule.add((short) 2);

        assertEquals(rule, newRule);
        assertNotSame(rule, newRule);
        assertEquals(rule.getCoveredInstances(), newRule.getCoveredInstances());
        assertNotSame(rule.getCoveredInstances(), newRule.getCoveredInstances());

    }

    @Test
    public void getMeritReturnsMerit() {
        Rule rule = new Rule((short) 3);
        rule.add((short) 2);
        rule.incrementHits();
        rule.incrementMisses();
        rule.incrementMisses();
        double n = 3;

        assertEquals(rule.getMerit(), (n - 2) / n, 0.01);
    }

    @Test
    public void getPessimisticErrorEstimateReturnsError() {
        Rule rule = new Rule((short) 3);
        rule.add((short) 2);
        rule.incrementHits();
        rule.incrementMisses();
        rule.incrementMisses();
        rule.incrementMisses();

        assertEquals(3.8, rule.getPessimisticErrorEstimate(), 0.01);
    }

    @Test
    public void toStringIncludesHitsMissesPer() {
        Rule rule = new Rule((short) 3);
        rule.add((short) 2);
        rule.incrementHits();
        rule.incrementMisses();
        rule.incrementMisses();
        assertThat(rule.toString(), CoreMatchers.containsString("hits: 1 misses: 2 per:"));
    }

}
