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
package lac.algorithms.l3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {
    @Test
    public void supportAntecedentCanBeSet() {
        Rule rule = new Rule(new short[] { 0 }, (short) 4);
        rule.setSupportAntecedent(3L);
        assertEquals(3L, rule.getSupportAntecedent());
    }

    @Test
    public void supportRuleCanBeSet() {
        Rule rule = new Rule(new short[] { 0 }, (short) 4);
        rule.setSupportRule(2L);
        assertEquals(2L, rule.getSupportRule());
    }

    @Test
    public void supportKlassCanBeSet() {
        Rule rule = new Rule(new short[] { 0 }, (short) 4);
        rule.setSupportKlass(4L);
        assertEquals(4L, rule.getSupportKlass());
    }

    @Test
    public void constructors() {
        Rule rule1 = new Rule(new Short[] { 0 }, (short) 4);
        Rule rule2 = new Rule(new short[] { 0 }, (short) 4);

        assertEquals(rule1.getAntecedent(), rule2.getAntecedent());
    }
}
