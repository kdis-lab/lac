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
package lac.algorithms.mac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {
    @Test
    public void constructorsSetSupports() {
        Rule rule = new Rule((short) 1);
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule((short) 1, (short) 2);
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule(new Short[] { 1 }, new Short((short) 2));
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportRule());

        rule = new Rule(new Short[] { 1 }, new HashSet<Integer>(Arrays.asList(1, 2)), new Short((short) 2),
                new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(2, rule.getSupportAntecedent());
        assertEquals(3, rule.getSupportRule());

        rule = new Rule((short) 2, new HashSet<Integer>(Arrays.asList(1, 2)), new Short((short) 2),
                new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(2, rule.getSupportAntecedent());
        assertEquals(3, rule.getSupportRule());
    }

    @Test
    public void setTidsetAntecedentUpdatesSupportAntecedent() {
        Rule rule = new Rule((short) 1);

        rule.setTidsetAntecedent(new HashSet<Integer>(Arrays.asList(1, 2)));
        assertEquals(2, rule.getSupportAntecedent());

        rule.setTidsetAntecedent(new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(3, rule.getSupportAntecedent());
    }

    @Test
    public void setTidsetUpdatesSupport() {
        Rule rule = new Rule((short) 1);

        rule.setTidsetRule(new HashSet<Integer>(Arrays.asList(1, 2)));
        assertEquals(2, rule.getSupportRule());

        rule.setTidsetRule(new HashSet<Integer>(Arrays.asList(1, 2, 3)));
        assertEquals(3, rule.getSupportRule());
    }
    
    @Test
    public void isCombinableReturnsTrueWithSameKlass() {
        assertTrue(new Rule((short)1).isCombinable(new Rule((short) 1)));
    }

    @Test
    public void isCombinableReturnsFalseWithDifferentKlass() {
        assertFalse(new Rule((short)1).isCombinable(new Rule((short) 2)));
    }
}
