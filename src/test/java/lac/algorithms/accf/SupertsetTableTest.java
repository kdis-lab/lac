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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class SupertsetTableTest extends TestSuite {
    private SupersetTable table;

    @Before
    public void setup() {
        table = new SupersetTable();
        Itemset itemset = new Itemset(new Short[] { 0, 1 }, 2L);
        BitSet bitset = new BitSet(3);
        bitset.set(0);
        table.add(itemset, bitset);
    }

    @Test
    public void isSupersetReturnsTrueWithSuperset() {
        Itemset itemset = new Itemset(new Short[] { 0, 1, 3 }, 1L);
        BitSet bitset = new BitSet(3);
        bitset.set(0);

        assertTrue(table.isSuperset(itemset, bitset));
    }

    @Test
    public void isSupersetReturnsFalseWithSubset() {
        Itemset itemset = new Itemset(new Short[] { 0 }, 2L);
        BitSet bitset = new BitSet(3);
        bitset.set(1);
        bitset.set(0);

        assertFalse(table.isSuperset(itemset, bitset));
    }

    @Test
    public void addExpandsItemset() {
        Itemset newItemset = new Itemset(new Short[] { 1 }, 1L);
        BitSet newBitset = new BitSet();
        table.add(newItemset, newBitset);

        Itemset itemset = new Itemset(new Short[] { 1, 2 }, 1L);
        BitSet bitset = new BitSet();

        assertTrue(table.isSuperset(itemset, bitset));
    }
}
