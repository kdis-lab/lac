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
import static org.junit.Assert.assertNotEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class BitSetSupportTest extends TestSuite {
    private BitSetSupport bitset1;
    private BitSetSupport bitset2;
    private BitSet expectedBitset;

    @Before
    public void setup() {
        bitset1 = new BitSetSupport();
        bitset1.bitset = new BitSet(4);
        bitset1.bitset.set(0);
        bitset1.bitset.set(2);
        bitset1.support = 2;

        bitset2 = new BitSetSupport();
        bitset2.bitset = new BitSet(4);
        bitset2.bitset.set(0);
        bitset2.bitset.set(1);
        bitset2.support = 2;

        expectedBitset = new BitSet(4);
        expectedBitset.set(0);
    }

    @Test
    public void andUpdatesSupport() {
        BitSetSupport result = bitset1.and(bitset2);
        assertEquals(1, result.support);
    }

    @Test
    public void andCalculateNewBitset() {
        BitSetSupport result = bitset1.and(bitset2);
        assertEquals(expectedBitset, result.bitset);
        assertNotEquals(bitset1.bitset, result.bitset);
        assertNotEquals(bitset2.bitset, result.bitset);
    }
}
