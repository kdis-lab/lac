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
package lac.utils;

import junit.framework.TestSuite;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class UtilsTest extends TestSuite {
    @Test
    public void intersectionReturnsArrayWithIntersection() {
        Set<Integer> set1 = new HashSet<Integer>();
        set1.add(1);
        set1.add(2);
        set1.add(3);
        Set<Integer> set2 = new HashSet<Integer>();
        set2.add(7);
        set2.add(2);
        set2.add(4);

        Set<Integer> expected = new HashSet<Integer>();
        expected.add(2);
        
        assertEquals(expected, Utils.intersection(set1, set2));
        assertEquals(expected, Utils.intersection(set2, set1));
    }

    @Test
    public void testIsSubsetBothArrayList() {
        ArrayList<Short> itemset1 = new ArrayList<Short>(Arrays.asList(new Short((short) 1), new Short((short) 2)));
        ArrayList<Short> itemset2 = new ArrayList<Short>(
                Arrays.asList(new Short((short) 1), new Short((short) 2), new Short((short) 3)));
        assertEquals(Utils.isSubset(itemset2, itemset1), true);

        ArrayList<Short> itemset3 = new ArrayList<Short>(
                Arrays.asList(new Short((short) 1), new Short((short) 4), new Short((short) 3)));
        assertEquals(Utils.isSubset(itemset3, itemset1), false);
    }

    @Test
    public void testIsSubsetFirstArrayList() {
        ArrayList<Short> itemset1 = new ArrayList<Short>(Arrays.asList(new Short((short) 1), new Short((short) 2)));
        Short[] itemset2 = { 1, 2, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset2), true);

        Short[] itemset3 = { 1, 4, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset3), false);
    }

    @Test
    public void testIsSubsetBothArrayObject() {
        Short[] itemset1 = { 1, 2 };
        Short[] itemset2 = { 1, 2, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset2), true);

        Short[] itemset3 = { 1, 4, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset3), false);
    }

    @Test
    public void testIsSubsetBothArray() {
        short[] itemset1 = { 1, 2 };
        short[] itemset2 = { 1, 2, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset2), true);

        short[] itemset3 = { 1, 4, 3 };
        assertEquals(Utils.isSubset(itemset1, itemset3), false);
    }

    @Test
    public void testConcatenate() {
        Short[] itemset1 = { 1, 2 };
        Short[] itemset2 = { 3, 4 };
        assertArrayEquals(Utils.concatenate(itemset1, itemset2), new Short[] { 1, 2, 3, 4 });
    }

    @Test
    public void testIntersect() {
        ArrayList<Short> itemset1 = new ArrayList<Short>(Arrays.asList(new Short((short) 1), new Short((short) 3)));
        Short[] itemset2 = { 3, 4 };
        assertEquals(Utils.intersect(itemset1, itemset2), new ArrayList<Short>(Arrays.asList((short) 3)));
    }

    @Test
    public void testUnionArray() {
        Short[] itemset1 = { 1, 2 };
        Short[] itemset2 = { 1, 2, 3 };
        assertArrayEquals(Utils.union(itemset1, itemset2), new Short[] { 1, 2, 3 });
    }

    @Test
    public void testUnionArrayList() {
        ArrayList<Short> itemset1 = new ArrayList<Short>(Arrays.asList(new Short((short) 1), new Short((short) 2)));
        ArrayList<Short> itemset2 = new ArrayList<Short>(
                Arrays.asList(new Short((short) 1), new Short((short) 2), new Short((short) 3)));
        assertArrayEquals(Utils.union(itemset1, itemset2), new Short[] { 1, 2, 3 });
    }

    @Test
    public void testUnionArrayAndArrayList() {
        Short[] itemset1 = { 1, 2, 3 };
        ArrayList<Short> itemset2 = new ArrayList<Short>(Arrays.asList(new Short((short) 1), new Short((short) 2)));
        assertArrayEquals(Utils.union(itemset1, itemset2), new Short[] { 1, 2, 3 });
    }

    @Test
    public void copyArray() {
        Short[] itemset = { 1, 2, 3 };
        assertArrayEquals(Utils.copy(itemset), new Short[] { 1, 2, 3 });
    }

    @Test
    public void copyMatrix() {
        double[][] itemset = { { 1.0 }, { 2.0 } };
        double[][] copyItemset = { { 1.0 }, { 2.0 } };
        assertArrayEquals(Utils.copy(itemset), copyItemset);
    }

    @Test
    public void addNewElement() {
        short[] itemset = { 1, 2 };
        assertArrayEquals(Utils.addNewElement(itemset, (short) 3), new short[] { 1, 2, 3 });
    }
}
