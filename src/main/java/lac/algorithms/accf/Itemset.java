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

import java.util.Arrays;

import lac.utils.Utils;

/**
 * This class represents an itemset (a set of items) implemented as an array of
 * integers with a variable to store the support count of the itemset.
 */
public class Itemset {
    /**
     * Set of items
     */
    private Short[] itemset;

    /**
     * Support for this itemset
     */
    private long support = 0;

    /**
     * Constructor
     * 
     * @param items   forming the itemset
     * @param support for the whole itemset
     */
    public Itemset(Short[] items, long support) {
        this.itemset = items;
        this.support = support;
    }

    /**
     * Get the support
     * 
     * @return support for current itemset
     */
    public long getSupport() {
        return support;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(itemset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return this.hashCode() == ((Itemset) o).hashCode();
    }

    /**
     * Check if contains all the items from passed itemset
     * 
     * @param itemset2 to be checked
     * @return true if contains all
     */
    public boolean containsAll(Itemset itemset2) {
        return Utils.isSubset(itemset2.itemset, this.itemset);
    }
}
