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
package lac.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class AttributeTest extends TestSuite {
    private Attribute nominalAttr;
    private Attribute numericAttr;
    private String[] nominalValues = { "val1", "val2" };

    @Before
    public void setup() {
        nominalAttr = new Attribute("nominalAttr", nominalValues);
        numericAttr = new Attribute("numericAttr", Attribute.TYPE_NUMERIC);
    }

    @Test
    public void isNominalReturnsTrueWhenNominal() {
        assertTrue(nominalAttr.isNominal());
    }

    @Test
    public void isNominalReturnsFalseWhenNumeric() {
        assertFalse(numericAttr.isNominal());
    }

    @Test
    public void isNumericReturnsFalseWhenNominal() {
        assertFalse(nominalAttr.isNumeric());
    }

    @Test
    public void isNumericReturnsTrueWhenNumeric() {
        assertTrue(numericAttr.isNumeric());
    }

    @Test
    public void getNumberValuesWhenNominalReturnsValues() throws Exception {
        assertEquals(nominalValues.length, nominalAttr.getNumberValues());
    }

    @Test(expected = Exception.class)
    public void getNumberValuesWhenNumericRaisesException() throws Exception {
        numericAttr.getNumberValues();
    }

    @Test
    public void getValues() {
        assertArrayEquals(nominalValues, nominalAttr.getValues());
    }

    @Test
    public void getString() {
        assertEquals("name=nominalAttr values=" + String.join(",", nominalValues), nominalAttr.toString());
        assertEquals("name=numericAttr", numericAttr.toString());
    }

    @Test
    public void getNameRetursnName() {
        assertEquals("nominalAttr", nominalAttr.getName());
        assertEquals("numericAttr", numericAttr.getName());
    }
}
