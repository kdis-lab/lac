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
package lac.algorithms.cpar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import junit.framework.TestSuite;

public class WeightedInstanceTest extends TestSuite {
    @Test
    public void cloneReturnsNewInstance() throws CloneNotSupportedException {
        WeightedInstance weighted = new WeightedInstance(new short[] { 0, 1 }, 0.2);

        assertArrayEquals(weighted.instance, ((WeightedInstance) weighted.clone()).instance);
        assertEquals(weighted.weight, ((WeightedInstance) weighted.clone()).weight, 0.01);
        assertNotSame(weighted, weighted.clone());
    }

    @Test
    public void weightIsInitalizedAs1_0() {
        WeightedInstance weighted = new WeightedInstance(new Short[] { 0, 1 });
        assertEquals(1.0, weighted.weight, 0.01);
    }
}
