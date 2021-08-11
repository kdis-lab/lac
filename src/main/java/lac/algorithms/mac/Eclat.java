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
package lac.algorithms.mac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class implementing the well-known ECLAT algorithm. Presented at: M. J. Zaki,
 * “Scalable algorithms for association mining.”IEEE Trans. Knowl. DataEng.,
 * vol. 12, no. 3, pp. 372–390, 2000. This is an adaptation for ACAC algorithm.
 * The key differences are:
 * <ul>
 * <li>It searches for class association rules</li>
 * <li>No patterns are generated, but rules are mined directly without an
 * intermediary step for searching for patterns</li>
 * </ul>
 */
public class Eclat {
    /**
     * Support relative to the dataset
     */
    private double minsupRelative;

    /**
     * Configuration used to generate class association rules
     */
    private Config config;

    /**
     * Dataset to extract class association rules
     */
    private Dataset dataset;

    /**
     * Set of returned rules
     */
    private ArrayList<Rule> rules = new ArrayList<Rule>();

    /**
     * Classes and its respective TIDS
     */
    private Map<Short, Set<Integer>> klassesTIDS;

    /**
     * Default constructor
     * 
     * @param dataset of the rules are mined
     * @param config  to be used while mining rules
     */
    public Eclat(Dataset dataset, Config config) {
        this.config = config;
        this.dataset = dataset;

        this.minsupRelative = Math.ceil(this.config.getMinSup() * dataset.size());
    }

    /**
     * Extracts class association rules from the previously set dataset
     * 
     * @return set of mined class association rules
     */
    public ArrayList<Rule> run() {
        HashMap<Short, Set<Integer>> mapItemTIDS = this.generateSingletons();

        ArrayList<Short> frequentItems = new ArrayList<Short>(mapItemTIDS.keySet());

        // Select only those items whose support is higher than the user-specified
        // threshold
        frequentItems.removeIf(item -> mapItemTIDS.get(item).size() < this.minsupRelative);

        // Sort items by the total order of increasing support
        Collections.sort(frequentItems, new Comparator<Short>() {
            public int compare(Short arg0, Short arg1) {
                return mapItemTIDS.get(arg0).size() - mapItemTIDS.get(arg1).size();
            }
        });

        // Generate rules of size k=2, that is, antecedent has one unique attribute and
        // one klass in the consequent
        ArrayList<Rule> k2 = this.generateK2(frequentItems, mapItemTIDS);

        for (int i = 0; i < k2.size(); i++) {
            Rule itemI = k2.get(i);

            ArrayList<Rule> prefixedItemsI = new ArrayList<Rule>();

            for (int j = i + 1; j < k2.size(); j++) {
                Rule itemJ = k2.get(j);

                if (!itemI.isCombinable(itemJ))
                    continue;

                Set<Integer> tidsetIJ = Utils.intersection(itemI.getTidsetRule(), itemJ.getTidsetRule());

                if (tidsetIJ.size() >= minsupRelative) {
                    Short[] newAntecedent = lac.utils.Utils.union(itemI.getAntecedent(), itemJ.getAntecedent());
                    Set<Integer> tidsetAntecedent = Utils.intersection(itemI.getTidsetAntecedent(),
                            itemJ.getTidsetAntecedent());

                    prefixedItemsI.add(new Rule(newAntecedent, tidsetAntecedent, itemI.getKlass(), tidsetIJ));
                }
            }

            // Process all prefixedItems, if there are
            if (!prefixedItemsI.isEmpty()) {
                processPrefixedItems(itemI, prefixedItemsI);
            }
        }

        return rules;
    }

    /**
     * Generate rules of size 2. Where the antecedent has one attribute, and the
     * consequent one class
     * 
     * @param frequentSingleton List of frequent singletons
     * @param mapItemTIDS       map with both singletons and its tidsets
     * @return array of rule of size 2
     */
    private ArrayList<Rule> generateK2(ArrayList<Short> frequentSingleton, HashMap<Short, Set<Integer>> mapItemTIDS) {
        ArrayList<Rule> k2 = new ArrayList<Rule>();

        for (int i = 0; i < frequentSingleton.size(); i++) {
            Short itemI = frequentSingleton.get(i);
            // we obtain the tidset and support of that item
            Set<Integer> tidsetI = mapItemTIDS.get(itemI);

            for (Entry<Short, Set<Integer>> klass : klassesTIDS.entrySet()) {
                Set<Integer> tidsetIJ = Utils.intersection(tidsetI, klass.getValue());

                Rule rule = new Rule(itemI, tidsetI, klass.getKey(), tidsetIJ);

                saveRule(rule);

                k2.add(rule);
            }
        }

        return k2;
    }

    /**
     * Scan the database to calculate the support of each singleton
     *
     * @return set of items and its tidsets
     */
    private HashMap<Short, Set<Integer>> generateSingletons() {
        HashMap<Short, Set<Integer>> itemTids = new HashMap<Short, Set<Integer>>();

        klassesTIDS = new HashMap<Short, Set<Integer>>();

        for (int indexInstance = 0; indexInstance < this.dataset.size(); indexInstance++) {
            for (int j = 0; j < this.dataset.getNumberAttributes(); j++) {
                Short singleton = (Short) this.dataset.getInstance(indexInstance).asNominal()[j];

                Set<Integer> tidset = itemTids.get(singleton);

                // add the new set, if there were none
                if (tidset == null) {
                    tidset = new HashSet<Integer>();
                    itemTids.put(singleton, tidset);
                }

                // Add the current tid
                tidset.add(indexInstance);
            }

            Short klass = this.dataset.getKlassInstance(indexInstance);

            Set<Integer> tidset = klassesTIDS.get(klass);

            // add the new set, if there were none
            if (tidset == null) {
                tidset = new HashSet<Integer>();
                klassesTIDS.put(klass, tidset);
            }

            // Add the current tid
            tidset.add(indexInstance);
        }

        final List<Short> items = new ArrayList<Short>(itemTids.keySet());
        for (Short entry : klassesTIDS.keySet()) {
            items.add(entry);
        }

        // Check from the previous items, which are frequent
        List<Short> frequentItems = new ArrayList<Short>();

        for (Entry<Short, Set<Integer>> entry : itemTids.entrySet()) {
            Set<Integer> tidset = entry.getValue();
            long support = tidset.size();

            if (support >= minsupRelative) {
                Short item = entry.getKey();

                // This item is frequent, since its support is greater than the user-specified
                // threshold
                frequentItems.add(item);
            }
        }

        return itemTids;
    }

    /**
     * Process all the prefixed items to generate much larger rules
     * 
     * @param rule          to be combined together with the prefixedItems
     * @param prefixedItems items prefixed to the current rule
     */
    private void processPrefixedItems(Rule rule, ArrayList<Rule> prefixedItems) {
        if (prefixedItems.size() == 1) {
            Rule itemI = prefixedItems.get(0);

            Short[] newAntecedent = lac.utils.Utils.union(rule.getAntecedent(), itemI.getAntecedent());
            Set<Integer> newTidset = Utils.intersection(rule.getTidsetRule(), itemI.getTidsetRule());
            Set<Integer> newTidsetAntecedent = Utils.intersection(rule.getTidsetAntecedent(),
                    itemI.getTidsetAntecedent());

            saveRule(new Rule(newAntecedent, newTidsetAntecedent, rule.getKlass(), newTidset));
        } else if (prefixedItems.size() == 2) {
            Rule itemI = prefixedItems.get(0);

            Short[] newAntecedent = lac.utils.Utils.union(rule.getAntecedent(), itemI.getAntecedent());
            Set<Integer> newTidset = Utils.intersection(rule.getTidsetRule(), itemI.getTidsetRule());
            Set<Integer> newTidsetAntecedent = Utils.intersection(rule.getTidsetAntecedent(),
                    itemI.getTidsetAntecedent());

            saveRule(new Rule(newAntecedent, newTidsetAntecedent, rule.getKlass(), newTidset));

            Rule itemJ = prefixedItems.get(1);

            Short[] newAntecedent2 = lac.utils.Utils.union(rule.getAntecedent(), itemJ.getAntecedent());
            Set<Integer> newTidset2 = Utils.intersection(rule.getTidsetRule(), itemJ.getTidsetRule());
            Set<Integer> newTidsetAntecedent2 = Utils.intersection(rule.getTidsetAntecedent(),
                    itemJ.getTidsetAntecedent());

            saveRule(new Rule(newAntecedent2, newTidsetAntecedent2, rule.getKlass(), newTidset2));

            // Union of the two previous items
            Short[] unionAntecedent = lac.utils.Utils.union(newAntecedent, itemJ.getAntecedent());
            if (unionAntecedent.length <= this.dataset.getNumberAttributes()) {
                Set<Integer> tidsetIJ = Utils.intersection(newTidset, itemJ.getTidsetRule());
                Set<Integer> antecedentTidsetIJ = Utils.intersection(newTidsetAntecedent, itemJ.getTidsetAntecedent());

                long supportIJ = tidsetIJ.size();

                if (supportIJ >= minsupRelative) {
                    saveRule(new Rule(unionAntecedent, antecedentTidsetIJ, rule.getKlass(), tidsetIJ));
                }
            }
        } else {
            // Combines each pair of rules to generate larger rules
            for (int i = 0; i < prefixedItems.size(); i++) {
                Rule itemI = prefixedItems.get(i);

                Short[] newAntecedent = lac.utils.Utils.union(rule.getAntecedent(), itemI.getAntecedent());
                Set<Integer> newTidset = Utils.intersection(rule.getTidsetRule(), itemI.getTidsetRule());
                Set<Integer> newTidsetAntecedent = Utils.intersection(rule.getTidsetAntecedent(),
                        itemI.getTidsetAntecedent());

                saveRule(new Rule(newAntecedent, newTidsetAntecedent, rule.getKlass(), newTidset));

                ArrayList<Rule> prefixedItemsSuffix = new ArrayList<Rule>();

                for (int j = i + 1; j < prefixedItems.size(); j++) {
                    Rule suffixJ = prefixedItems.get(j);

                    Set<Integer> tidsetIJ = Utils.intersection(newTidset, suffixJ.getTidsetRule());

                    Short[] new2Antecedent = lac.utils.Utils.union(newAntecedent, suffixJ.getAntecedent());

                    long supportIJ = tidsetIJ.size();
                    if (supportIJ >= minsupRelative) {
                        Set<Integer> tidsetAntecedent = Utils.intersection(newTidsetAntecedent,
                                suffixJ.getTidsetAntecedent());

                        prefixedItemsSuffix.add(new Rule(new2Antecedent, tidsetAntecedent, rule.getKlass(), tidsetIJ));
                    }
                }

                // Recursively process the rest of prefixed items to generate even larger rules
                if (!prefixedItemsSuffix.isEmpty()) {
                    processPrefixedItems(new Rule(newAntecedent, newTidsetAntecedent, rule.getKlass(), newTidset),
                            prefixedItemsSuffix);
                }
            }
        }
    }

    /**
     * Add a rule to the final set of rules if its confidence is greater than the
     * user-specified threshold
     * 
     * @param rule
     */
    private void saveRule(Rule rule) {
        if (rule.getConfidence() >= this.config.getMinConf()) {
            rules.add(rule);
        }
    }
}
