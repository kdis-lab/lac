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
package lac.algorithms.cba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Dataset;

/**
 * Main for the Apriori algorithm. This algorithm has been adapted to obtain
 * class association rules instead of patterns, whose support and confidence are
 * greater than its thresholds.
 */
public class Apriori {
    /**
     * Threshold of frequency of occurrence for the current dataset being mined
     */
    private long minSupRelative;

    /**
     * Configuration used to generate rules
     */
    private Config config;

    /**
     * Train dataset where find rules to form the associative classifier
     */
    private Dataset dataset;

    /**
     * Auxiliary class used to represent singletons and its support
     */
    public class Item {
        /**
         * Item
         */
        short item;

        /**
         * Frequency of occurrence
         */
        long support;

        /**
         * Constructor
         * 
         * @param item    singleton
         * @param support frequency of occurrence for this item
         */
        public Item(short item, long support) {
            this.item = item;
            this.support = support;
        }
    }

    /**
     * Default constructor
     * 
     * @param dataset to obtain rules
     * @param config  used to mine rules
     */
    public Apriori(Dataset dataset, Config config) {
        this.dataset = dataset;

        this.config = config;

        this.minSupRelative = (long) Math.ceil(config.getMinSup() * dataset.size());
    }

    /**
     * Extract class association rules from the previously set dataset
     * 
     * @return rules whose support and confidence is greater than a user-specified
     *         threshold
     */
    public ArrayList<Rule> run() {
        ArrayList<Rule> rules = new ArrayList<Rule>();

        ArrayList<Item> frequent1 = this.generateSingletons();

        // We sort the list of candidates by lexical order
        // (Apriori need to use a total order otherwise it does not work)
        Collections.sort(frequent1, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o1.item - o2.item;
            }
        });

        // If no frequent item, there are no need to follow searching (anti-monotone
        // property).
        if (frequent1.isEmpty()) {
            return new ArrayList<Rule>();
        }

        ArrayList<Rule> level = null;
        int k = 2;
        do {
            // Generate candidates of size K
            ArrayList<Rule> candidatesK;

            // Level 2 could be generated in a optimized way, thus, it has been separated to
            // another function
            if (k == 2) {
                candidatesK = generateCandidate2(frequent1);
            } else {
                candidatesK = generateCandidateSizeK(level);
            }

            // Scan the database to calculate support for each candidate
            for (Rule candidate : candidatesK) {
                candidate.calculateSupports(dataset);
            }

            level = new ArrayList<Rule>();
            for (Rule candidate : candidatesK) {
                if (candidate.getSupportRule() >= this.minSupRelative && !level.contains(candidate)) {
                    level.add(candidate);

                    if (candidate.getConfidence() >= this.config.getMinConf()) {
                        rules.add(candidate);
                    }
                }
            }
            k++;
        } while (!level.isEmpty());

        return rules;
    }

    /**
     * Generate singletons and its frequency. Only frequent singletons are
     * considered
     * 
     * @return singletons and its frequency
     */
    private ArrayList<Item> generateSingletons() {
        HashMap<Short, Long> mapItemCount = new HashMap<Short, Long>();

        for (int i = 0; i < dataset.size(); i++) {
            Short[] example = dataset.getInstance(i).asNominal();

            // -1 because klass should not be saved in mapItemCount
            for (int j = 0; j < example.length - 1; j++) {
                short item = example[j];

                // increase the support count
                Long count = mapItemCount.getOrDefault(item, 0L);
                mapItemCount.put(item, ++count);
            }
        }

        // We add all frequent items to the set of candidate of size 1
        ArrayList<Item> frequent1 = new ArrayList<Item>();
        for (Entry<Short, Long> entry : mapItemCount.entrySet()) {
            if (entry.getValue() >= this.minSupRelative) {
                frequent1.add(new Item(entry.getKey(), entry.getValue()));
            }
        }

        return frequent1;
    }

    /**
     * This method generates rules whose antecedent has size 2
     * 
     * @param frequent1 the list of frequent singletons of size 1
     * @return a List of Rule that are the candidates
     */
    private ArrayList<Rule> generateCandidate2(ArrayList<Item> frequent1) {
        ArrayList<Rule> candidates = new ArrayList<Rule>();

        // For each itemset I1 and I2 of level k-1
        for (int i = 0; i < frequent1.size(); i++) {
            Short item1 = frequent1.get(i).item;

            Rule rule = new Rule();
            rule.add(item1);
            for (int j = 0; j < dataset.getNumberKlasses(); j++) {
                short klass = dataset.getKlass(j);
                rule.setKlass(klass);
                candidates.add((Rule) rule.clone());
            }
        }
        return candidates;
    }

    /**
     * Method to generate rules of size k from frequent rules of size K-1.
     * 
     * @param levelK_1 frequent rules of size k-1
     * @return candidates rules of size k, where all its subsets are frequent.
     */
    protected ArrayList<Rule> generateCandidateSizeK(ArrayList<Rule> levelK_1) {
        // create a variable to store candidates
        ArrayList<Rule> candidates = new ArrayList<Rule>();

        // For each itemset I1 and I2 of level k-1
        for (int i = 0; i < levelK_1.size(); i++) {
            Rule rule1 = levelK_1.get(i);
            for (int j = i + 1; j < levelK_1.size(); j++) {
                Rule rule2 = levelK_1.get(j);

                if (!rule1.isCombinable(rule2))
                    continue;

                // Create a new candidate by combining itemset1 and itemset2
                Rule newRule = (Rule) rule1.clone();
                newRule.add((rule2.get(rule2.size() - 1)));

                // The candidate is tested to see if its subsets of size k-1 are
                // included in
                // level k-1 (they are frequent).
                if (areSubsetsFrequents(newRule, levelK_1)) {
                    candidates.add(newRule);
                }
            }
        }
        return candidates;
    }

    /**
     * Method to check if all the subsets of size k-1 of a candidate are frequent.
     * That is a requirement of the anti-monotone property of the support
     * 
     * @param candidate a candidate rule of size k
     * @param levelK_1  the frequent rules of size k-1
     * @return true if all the subsets are frequent
     */
    @SuppressWarnings("unchecked")
    protected boolean areSubsetsFrequents(Rule candidate, ArrayList<Rule> levelK_1) {
        // generate all subsets by always each item from the candidate, one by one
        for (int positionToRemove = 0; positionToRemove < candidate.getAntecedent().size(); positionToRemove++) {
            ArrayList<Short> subset = (ArrayList<Short>) candidate.getAntecedent().clone();
            subset.remove(positionToRemove);

            boolean found = levelK_1.stream().filter(rule -> rule.getAntecedent().containsAll(subset)).findFirst()
                    .isPresent();

            if (!found) {
                return false;
            }
        }
        return true;
    }
}
