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
 * This algorithm was adapted from the SPMF Library, which is is licensed 
 * under the GNU GPL v3 license.
 * Fournier-Viger, P., Lin, C.W., Gomariz, A., Gueniche, T., Soltani, A., 
 * Deng, Z., Lam, H. T. (2016). The SPMF Open-Source Data Mining Library 
 * Version 2. Proc. 19th European Conference on Principles of Data Mining 
 * and Knowledge Discovery (PKDD 2016) Part III, Springer LNCS 9853, pp. 36-40.
 */
package lac.algorithms.accf;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Class used to represent a superset table used in CHARM to check if itemsets
 * are closed or not. It enables to speed-up testing.
 * 
 */
class SupersetTable {
    /**
     * Field used to store the table itself
     */
    private ArrayList<Itemset>[] table;

    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked")
    public SupersetTable() {
        table = new ArrayList[1];
    }

    /**
     * Check if itemset is superset or not
     * 
     * @param itemset to be checked if it is superset
     * @param bitset  to check if it is contained
     * @return false when it is not superset, true otherwise
     */
    public boolean isSuperset(Itemset itemset, BitSet bitset) {
        int hashcode = calculateHashCode(bitset);

        if (table[hashcode] == null) {
            return true;
        }

        for (Object object : table[hashcode]) {
            Itemset currentItemset = (Itemset) object;
            if (currentItemset.getSupport() == itemset.getSupport() && currentItemset.containsAll(itemset)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add an itemset to the hash table
     * 
     * @param itemset to be added to the table
     * @param bitset  bitset to calculate hashcode
     */
    public void add(Itemset itemset, BitSet bitset) {
        int hashcode = calculateHashCode(bitset);

        if (table[hashcode] == null) {
            table[hashcode] = new ArrayList<Itemset>();
        }
        table[hashcode].add(itemset);
    }

    /**
     * Calculate a hashcode to effectively check supersets
     * 
     * @param bitset to calculate hashcode
     * @return the hashcode
     */
    private int calculateHashCode(BitSet bitset) {
        int hashCode = 0;
        // For each bit in the bitset, sum it
        for (int bid = bitset.nextSetBit(0); bid >= 0; bid = bitset.nextSetBit(bid + 1)) {
            hashCode += bid;
        }

        // Negative hashcode are converted to positive
        if (hashCode < 0) {
            hashCode = -1 * hashCode;
        }

        // Module with the size of the table
        return hashCode % table.length;
    }
}
