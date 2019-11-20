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
package lac.algorithms.accf;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {

    @Test
    public void supportsCouldBeSetFromOutside() {
        Rule rule = new Rule(new short[] { 0 }, (short) 1);
        rule.setSupportAntecedent(1L);
        rule.setSupportKlass(2L);
        rule.setSupportRule(3L);

        assertEquals(1, rule.getSupportAntecedent());
        assertEquals(2, rule.getSupportKlass());
        assertEquals(3, rule.getSupportRule());
    }

    @Test
    public void antecedentAndKlassConstructor() {
        Rule rule = new Rule(new short[] { 0 }, (short) 1);
        assertEquals(new ArrayList<Short>(Arrays.asList(Short.valueOf((short) 0))), rule.getAntecedent());
        assertEquals(1, rule.getKlass());

        rule = new Rule(new Short[] { 1 }, (short) 2);
        assertEquals(new ArrayList<Short>(Arrays.asList(Short.valueOf((short) 1))), rule.getAntecedent());
        assertEquals(2, rule.getKlass());
    }
}
