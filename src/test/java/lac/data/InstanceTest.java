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

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class InstanceTest extends TestSuite {
    private Instance instance;
    private Short[] values = { 1, 2 };

    @Before
    public void setup() {
        instance = new Instance(values.length);

        for (int i = 0; i < values.length; i++)
            instance.set(i, values[i]);
    }

    @Test
    public void asNominal() {
        assertArrayEquals(values, instance.asNominal());
    }

    @Test
    public void getKlass() {
        assertEquals(values[values.length - 1], instance.getKlass());
    }

    @Test
    public void setKlass() {
        Short newKlass = 1000;

        assertEquals(values[values.length - 1], instance.getKlass());
        instance.setKlass(newKlass);
        assertEquals(newKlass, instance.getKlass());
    }
}
