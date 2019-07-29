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

import java.util.BitSet;

/**
 * Anonymous inner class to store a bitset and its cardinality. Storing the
 * cardinality is useful because the cardinality() method of a bitset in Java is
 * very expensive.
 */
public class BitSetSupport {
    BitSet bitset = new BitSet();
    long support;

    /**
     * Perform the intersection of two tidsets for itemsets containing more than one
     * item.
     * 
     * @param tidsetI the first tidset
     * @param tidsetJ the second tidset
     * @return the resulting tidset and its support
     */
    BitSetSupport and(BitSetSupport tidsetJ) {
        // Create the new tidset and perform the logical AND to intersect the tidset
        BitSetSupport bitsetSupportIJ = new BitSetSupport();
        bitsetSupportIJ.bitset = (BitSet) this.bitset.clone();
        bitsetSupportIJ.bitset.and(tidsetJ.bitset);
        // set the support as the cardinality of the new tidset
        bitsetSupportIJ.support = bitsetSupportIJ.bitset.cardinality();
        // return the new tidset
        return bitsetSupportIJ;
    }
}