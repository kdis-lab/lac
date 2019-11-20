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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ItemsetTest extends TestSuite {
    private Itemset itemset;

    @Before
    public void setup() {
        itemset = new Itemset(new Short[] { 0, 1 }, 3L);
    }

    @Test
    public void obtainsSupport() {
        assertEquals(3L, itemset.getSupport());
    }

    @Test
    public void calculateHascodeUsingItemset() {
        assertEquals(Arrays.hashCode(new Short[] { 0, 1 }), itemset.hashCode());
    }

    @Test
    public void equalsIsCorrectlyImplemented() {
        assertEquals(new Itemset(new Short[] { 0, 1 }, 3L), itemset);
    }

    @Test
    public void containsAllReturnsTrueWhenAllAreContained() {
        Itemset itemset2 = new Itemset(new Short[] { 0 }, 3L);
        assertTrue(itemset.containsAll(itemset2));
    }

    @Test
    public void containsAllReturnsFalseWhenNotAllAreContained() {
        Itemset itemset2 = new Itemset(new Short[] { 0, 1, 6 }, 3L);
        assertFalse(itemset.containsAll(itemset2));
    }
}
