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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * An adaptation of the CHARM algorithm. It is able to directly obtain rules
 * without requiring to mine patterns
 */
public class CHARM {
    /**
     * Dataset being used to mine rules
     */
    private Dataset dataset;

    /**
     * Configuration used to obtain rules
     */
    private Config config;

    /**
     * Minimum relative support used to obtain rules
     */
    private long minSupRelative;

    /**
     * Bitset for each class
     */
    private HashMap<Short, BitSetSupport> klasses;

    /**
     * The hash table for storing itemsets for closeness checking (an optimization)
     */
    private SupersetTable supersetTable;

    /**
     * Obtained rules
     */
    private ArrayList<Rule> rules;

    /**
     * Default constructor
     * 
     * @param dataset used to obtain rules
     * @param config  used to obtain rules
     */
    public CHARM(Dataset dataset, Config config) {
        this.dataset = dataset;
        this.config = config;

        this.minSupRelative = (long) Math.ceil(this.config.getMinSup() * this.dataset.size());
    }

    /**
     * Run the algorithm and obtain class association rules
     * 
     */
    public ArrayList<Rule> run() {
        this.rules = new ArrayList<Rule>();

        // Create the hash table to store itemsets for closeness checking
        this.supersetTable = new SupersetTable();

        klasses = new HashMap<Short, BitSetSupport>();

        HashMap<Short, BitSetSupport> mapItemSingletons = this.generateSingletons();

        ArrayList<Short> frequentItems = new ArrayList<Short>();
        for (Entry<Short, BitSetSupport> entry : mapItemSingletons.entrySet()) {
            BitSetSupport bitset = entry.getValue();
            long support = bitset.support;
            Short item = entry.getKey();

            if (support >= this.minSupRelative) {
                frequentItems.add(item);
            }
        }

        // Sort the list of items by the total order of increasing support.
        Collections.sort(frequentItems, new Comparator<Short>() {
            @Override
            public int compare(Short arg0, Short arg1) {
                return (int) (mapItemSingletons.get(arg0).support - mapItemSingletons.get(arg1).support);
            }
        });

        for (int i = 0; i < frequentItems.size(); i++) {
            Short itemI = frequentItems.get(i);
            if (itemI == null)
                continue;

            BitSetSupport bitsetI = mapItemSingletons.get(itemI);

            Short[] itemsetI = new Short[] { itemI };

            ArrayList<Short[]> prefixedItems = new ArrayList<Short[]>();
            ArrayList<BitSetSupport> prefixedBitsets = new ArrayList<BitSetSupport>();

            for (int j = i + 1; j < frequentItems.size(); j++) {
                Short itemJ = frequentItems.get(j);
                if (itemJ == null)
                    continue;

                BitSetSupport bitsetJ = mapItemSingletons.get(itemJ);

                BitSetSupport bitsetUnion = bitsetI.and(bitsetJ);

                // Is the new pattern frequent?
                if (bitsetUnion.support < minSupRelative) {
                    continue;
                }

                // CHARM defines 4 different properties
                if (bitsetI.support == bitsetJ.support && bitsetUnion.support == bitsetI.support) {
                    // Property 1, where I is replaced by the new created union. j is removed
                    frequentItems.set(j, null);
                    Short[] union = new Short[itemsetI.length + 1];
                    System.arraycopy(itemsetI, 0, union, 0, itemsetI.length);
                    union[itemsetI.length] = itemJ;
                    itemsetI = union;
                } else if (bitsetI.support < bitsetJ.support && bitsetUnion.support == bitsetI.support) {
                    // Property 2, where I is replaced by he union. But I is not removed
                    Short[] union = new Short[itemsetI.length + 1];
                    System.arraycopy(itemsetI, 0, union, 0, itemsetI.length);
                    union[itemsetI.length] = itemJ;
                    itemsetI = union;
                } else if (bitsetI.support > bitsetJ.support && bitsetUnion.support == bitsetJ.support) {
                    // Property 3, where j is removed and union add to the prefixed items
                    frequentItems.set(j, null);
                    prefixedItems.add(new Short[] { itemJ });
                    prefixedBitsets.add(bitsetUnion);
                } else {
                    // Property4, union is added to prefixedItems
                    prefixedItems.add(new Short[] { itemJ });
                    prefixedBitsets.add(bitsetUnion);
                }
            }

            if (prefixedItems.size() > 0) {
                processPrefixedItems(itemsetI, prefixedItems, prefixedBitsets);
            }

            // Generate rules for current itemset
            generateRules(null, itemsetI, bitsetI);
        }

        return this.rules;
    }

    /**
     * Generate singletons and its bitset
     * 
     * @return map with both singletons and its bitset
     */
    private HashMap<Short, BitSetSupport> generateSingletons() {
        HashMap<Short, BitSetSupport> singletons = new HashMap<Short, BitSetSupport>();

        for (int indexInstance = 0; indexInstance < this.dataset.size(); indexInstance++) {
            for (int j = 0; j < this.dataset.getNumberAttributes(); j++) {
                Short item = this.dataset.getInstance(indexInstance).asNominal()[j];
                BitSetSupport tids = singletons.get(item);

                // Create a new one
                if (tids == null) {
                    tids = new BitSetSupport();
                    singletons.put(item, tids);
                }

                tids.bitset.set(indexInstance);
                tids.support++;
            }

            // Klass for current isntance
            Short klass = this.dataset.getKlassInstance(indexInstance);

            BitSetSupport tids = klasses.get(klass);
            if (tids == null) {
                tids = new BitSetSupport();
                klasses.put(klass, tids);
            }

            tids.bitset.set(indexInstance);
            tids.support++;

        }
        return singletons;
    }

    /**
     * Process all itemsets from a prefixed items to generate even larger itemsets
     * 
     * @param prefix
     * @param prefixedItemsets
     * @param prefixedBitsets
     */
    private void processPrefixedItems(Short[] prefix, ArrayList<Short[]> prefixedItemsets,
            ArrayList<BitSetSupport> prefixedBitsets) {
        if (prefixedItemsets.size() == 1) {
            Short[] itemsetI = prefixedItemsets.get(0);
            BitSetSupport bitsetI = prefixedBitsets.get(0);

            generateRules(prefix, itemsetI, bitsetI);
            return;
        } else if (prefixedItemsets.size() == 2) {
            Short[] itemsetI = prefixedItemsets.get(0);
            BitSetSupport bitsetI = prefixedBitsets.get(0);

            Short[] itemsetJ = prefixedItemsets.get(1);
            BitSetSupport bitsetJ = prefixedBitsets.get(1);

            BitSetSupport bitsetSupportIJ = bitsetI.and(bitsetJ);

            if (bitsetSupportIJ.support >= minSupRelative) {
                Short[] suffixIJ = Utils.concatenate(itemsetI, itemsetJ);
                generateRules(prefix, suffixIJ, bitsetSupportIJ);
            }

            // If support is not the same, it coulld be closed, so we have to generate rules
            if (bitsetSupportIJ.support != bitsetI.support) {
                generateRules(prefix, itemsetI, bitsetI);
            }
            if (bitsetSupportIJ.support != bitsetJ.support) {
                generateRules(prefix, itemsetJ, bitsetJ);
            }
            return;
        }

        // Combines each prefixed itemsets to generate even larger itemsets
        for (int i = 0; i < prefixedItemsets.size(); i++) {
            Short[] itemsetI = prefixedItemsets.get(i);
            if (itemsetI == null) {
                continue;
            }

            BitSetSupport bitsetI = prefixedBitsets.get(i);

            ArrayList<Short[]> prefixedIitemsets = new ArrayList<Short[]>();
            ArrayList<BitSetSupport> prefixedIBitsets = new ArrayList<BitSetSupport>();

            for (int j = i + 1; j < prefixedItemsets.size(); j++) {
                Short[] itemsetJ = prefixedItemsets.get(j);

                if (itemsetJ == null) {
                    continue;
                }
                BitSetSupport bitsetJ = prefixedBitsets.get(j);

                BitSetSupport bitsetUnion = new BitSetSupport();
                bitsetUnion = bitsetI.and(bitsetJ);

                // Check if union is frequent
                if (bitsetUnion.support < minSupRelative) {
                    continue;
                }

                // CHARM defines 4 different properties
                if (bitsetI.support == bitsetJ.support && bitsetUnion.support == bitsetI.support) {
                    // Property 1, where I is replaced by the new created union. j is removed
                    prefixedItemsets.set(j, null);
                    prefixedBitsets.set(j, null);
                    Short[] union = Utils.concatenate(itemsetI, itemsetJ);
                    itemsetI = union;
                } else if (bitsetI.support < bitsetJ.support && bitsetUnion.support == bitsetI.support) {
                    // Property 2, where I is replaced by he union. But I is not removed
                    Short[] union = Utils.concatenate(itemsetI, itemsetJ);
                    itemsetI = union;
                } else if (bitsetI.support > bitsetJ.support && bitsetUnion.support == bitsetJ.support) {
                    // Property 3, where j is removed and union add to the prefixed items
                    prefixedItemsets.set(j, null);
                    prefixedBitsets.set(j, null);
                    prefixedIitemsets.add(itemsetJ);
                    prefixedIBitsets.add(bitsetUnion);
                } else {
                    // Property4, union is added to prefixedItems
                    prefixedIitemsets.add(itemsetJ);
                    prefixedIBitsets.add(bitsetUnion);
                }
            }

            if (prefixedIitemsets.size() > 0) {
                Short[] newPrefix = Utils.concatenate(prefix, itemsetI);
                processPrefixedItems(newPrefix, prefixedIitemsets, prefixedIBitsets);
            }
            generateRules(prefix, itemsetI, bitsetI);
        }
    }

    /**
     * Generate rules from current prefix and suffix
     * 
     * @param prefix
     * @param suffix
     * @param bitset
     */
    private void generateRules(Short[] prefix, Short[] suffix, BitSetSupport bitset) {
        // Concatenate the suffix and prefix of that itemset.
        Short[] items;
        if (prefix == null) {
            items = suffix;
        } else {
            items = Utils.concatenate(prefix, suffix);
        }

        // Lexical order!
        Arrays.sort(items);

        Itemset itemset = new Itemset(items, bitset.support);

        // If there are not any superset, itemset is closed
        if (supersetTable.isSuperset(itemset, bitset.bitset)) {
            // Generate rules for current item
            for (Entry<Short, BitSetSupport> klass : klasses.entrySet()) {
                BitSetSupport klassbitset = klass.getValue();
                long supportKlass = klassbitset.support;
                Short itemKlass = klass.getKey();

                BitSetSupport bitsetRule = bitset.and(klassbitset);
                Rule rule = new Rule(items, itemKlass);
                rule.setSupportAntecedent(bitset.support);
                rule.setSupportRule(bitsetRule.support);
                rule.setSupportKlass(supportKlass);

                if (rule.getConfidence() >= this.config.getMinConf()) {
                    rules.add(rule);
                }
            }

            supersetTable.add(itemset, bitset.bitset);
        }
    }
}
