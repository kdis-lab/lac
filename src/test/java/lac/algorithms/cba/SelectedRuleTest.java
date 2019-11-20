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
package lac.algorithms.cba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.TestSuite;

public class SelectedRuleTest extends TestSuite {
    @Test
    public void getters() {
        Rule rule = new Rule((short) 3);
        SelectedRule sRule = new SelectedRule(rule, (short) 2, 50L);

        assertEquals(rule, sRule.getRule());
        assertEquals(new Long(50), sRule.getTotalErrors());
        assertEquals(2, sRule.getDefaultKlass());
    }

    @Test
    public void compateToGreaterReturnMinus1() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 50L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 40L);

        assertEquals(-1, sRule1.compareTo(sRule2));
    }

    @Test
    public void compateToGreaterReturn1() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 40L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 50L);

        assertEquals(1, sRule1.compareTo(sRule2));
    }

    @Test
    public void compateToGreaterReturn0() {
        SelectedRule sRule1 = new SelectedRule(null, (short) 2, 40L);
        SelectedRule sRule2 = new SelectedRule(null, (short) 2, 40L);

        assertEquals(0, sRule1.compareTo(sRule2));
    }

}
