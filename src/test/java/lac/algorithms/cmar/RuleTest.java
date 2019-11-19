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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import junit.framework.TestSuite;

public class RuleTest extends TestSuite {
    @Test
    public void isMoreGeneralUsesAntecedentSize() {
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        
        assertTrue(rule1.isMoreGeneral(rule2));
        assertFalse(rule2.isMoreGeneral(rule1));
    }
    
    @Test
    public void isGreaterUsesConfidence() {
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        rule1.setSupportRule(3);
        rule1.setSupportAntecedent(3);
        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        rule2.setSupportRule(2);
        rule2.setSupportAntecedent(3);
        
        assertTrue(rule1.isGreater(rule2));
        assertFalse(rule2.isGreater(rule1));
    }
    
    @Test
    public void isGreaterUsesSupportRule() {
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        rule1.setSupportRule(3);
        rule1.setSupportAntecedent(3);
        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        rule2.setSupportRule(2);
        rule2.setSupportAntecedent(2);
        
        assertTrue(rule1.isGreater(rule2));
        assertFalse(rule2.isGreater(rule1));
    }

    @Test
    public void isGreaterUsesSizeAntecedent() {
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        rule1.setSupportRule(3);
        rule1.setSupportAntecedent(3);
        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        rule2.setSupportRule(3);
        rule2.setSupportAntecedent(3);
        
        assertTrue(rule1.isGreater(rule2));
        assertFalse(rule2.isGreater(rule1));
    }

    @Test
    public void getChiSquare() {
        Rule.NUMBER_INSTANCES = 5.0;
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        rule1.setSupportRule(2);
        rule1.setSupportAntecedent(1);
        rule1.setSupportKlass(3);
        
        assertEquals(10.20, rule1.getChiSquare(), 0.01);

        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        rule2.setSupportRule(3);
        rule2.setSupportAntecedent(3);
        rule2.setSupportKlass(3);

        assertEquals(5.0, rule2.getChiSquare(), 0.01);
    }

    @Test
    public void getChiSquareUpperBound() {
        Rule.NUMBER_INSTANCES = 5.0;
        Rule rule1 = new Rule(new short[] {0}, (short) 4);
        rule1.setSupportRule(2);
        rule1.setSupportAntecedent(1);
        rule1.setSupportKlass(3);
        
        assertEquals(0.83, rule1.getChiSquareUpperBound(), 0.01);

        Rule rule2 = new Rule(new short[] {0, 1}, (short) 4);
        rule2.setSupportRule(3);
        rule2.setSupportAntecedent(3);
        rule2.setSupportKlass(3);

        assertEquals(5.0, rule2.getChiSquareUpperBound(), 0.01);
    }}
