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
 * This algorithm was taken from the SPMF Library, which is is licensed 
 * under the GNU GPL v3 license.
 * Fournier-Viger, P., Lin, C.W., Gomariz, A., Gueniche, T., Soltani, A., 
 * Deng, Z., Lam, H. T. (2016). The SPMF Open-Source Data Mining Library 
 * Version 2. Proc. 19th European Conference on Principles of Data Mining 
 * and Knowledge Discovery (PKDD 2016) Part III, Springer LNCS 9853, pp. 36-40.
 */
package lac.algorithms.acac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Dataset;

/**
 * Class implementing the well-known Apriori algorithm. Presented at: R.
 * Agrawal, T. Imielinkski, A. Swami. Mining association rules between sets of
 * items in large databases. SIGMOD. 1993. 207-216. This is an adaptation for
 * ACAC algorithm. The key differences are:
 * <ul>
 * <li>It searches for class association rules</li>
 * <li>No patterns are generated, but rules are mined directly without an
 * intermediary step for searching for patterns</li>
 * <li>It incorporates the calculation of all-confidence in the mined of class
 * association rules</li>
 * </ul>
 */
public class Apriori {

    /**
     * Auxiliary class used to represent singletons and its support
     */
    public class Item {
        short item;
        long support;

        public Item(short item, long support) {
            this.item = item;
            this.support = support;
        }
    }

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
     * Default constructor
     */
    public Apriori(Dataset dataset, Config config) {
        this.dataset = dataset;

        this.config = config;

        // Calculate support relative to the current dataset
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

        // Sort by lexical order
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
            // another
            // function
            if (k == 2) {
                candidatesK = generateCandidate2(frequent1);
            } else {
                // otherwise we use the regular way to generate candidates
                candidatesK = generateCandidateSizeK(level);
            }

            // Scan the database to calculate support for each candidate
            for (Rule candidate : candidatesK) {
                candidate.evaluate(dataset);
            }

            // Check if candidates are final rules or they have to be extended more
            level = new ArrayList<Rule>();
            for (Rule candidate : candidatesK) {
                // Candidates to be considered as finalRules they have to be frequent, and
                // all-confidence greather than threshold
                if (candidate.getSupportRule() >= this.minSupRelative
                        && candidate.getAllConfidence() >= this.config.getMinAllConf() && !level.contains(candidate)) {
                    // If rule has a higher confidence than threshold, it is used as final rule. In
                    // other case, rule has to be extended again
                    if (candidate.getConfidence() >= this.config.getMinConf()) {
                        rules.add(candidate);
                    } else {
                        level.add(candidate);
                    }
                }
            }

            // increment the size of the dataset
            k++;
        } while (!level.isEmpty());

        return rules;
    }

    /**
     * Generate singletons and its frequency. Only frequent singletons are
     * considered
     * 
     * @return
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
            Item item1 = frequent1.get(i);

            Rule rule = new Rule();
            rule.add(item1.item);
            for (int j = 0; j < dataset.getNumberKlasses(); j++) {
                short klass = dataset.getKlass(j);
                long supportKlass = this.dataset.getFrequencyByKlass().get(klass);

                rule.setKlass(klass);
                rule.setMaximums(item1.support, supportKlass);

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

                if (!isCombinable(rule1, rule2))
                    continue;

                // Create a new candidate by combining itemset1 and itemset2
                Rule newRule = (Rule) rule1.clone();
                newRule.add((rule2.get(rule2.size() - 1)));
                newRule.setMaximums(rule1.getSupportRule(), rule2.getSupportRule());

                // The candidate is tested to see if its subsets of size k-1 are
                // included in
                // level k-1 (they are frequent).
                if (areSubsetsFrequents(newRule, levelK_1)) {
                    candidates.add(newRule);
                }
            }
        }
        return candidates; // return the set of candidates
    }

    /**
     * Determines if two rules could be combined using apriori. That is, they follow
     * at the very least the following conditions: * Class is equal * Size of
     * antecedent is the same * Share size - 1 items in the antecedent * From the
     * not shared items, the item from ruleI has a greater lexicography order than
     * ruleJ
     * 
     * @param ruleI first rule to check if it could be combined
     * @param ruleJ second rule to check if it could be combined
     * @return boolean, with a value of true if two rules could be combined
     */
    protected boolean isCombinable(Rule ruleI, Rule ruleJ) {
        if (ruleI.getKlass() != ruleJ.getKlass())
            return false;

        if (ruleI.size() != ruleJ.size())
            return false;

        Short itemI, itemJ;
        for (int i = 0; i < ruleI.size() - 1; i++) {
            itemI = ruleI.get(i);
            itemJ = ruleJ.get(i);

            if (itemI != itemJ)
                return false;
        }

        itemI = ruleI.get(ruleI.size() - 1);
        itemJ = ruleJ.get(ruleJ.size() - 1);

        if (itemI >= itemJ)
            return false;

        return true;
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
        for (int positionToRemove = 0; positionToRemove < candidate.getAntecedent().size(); positionToRemove++) {
            ArrayList<Short> subset = (ArrayList<Short>) candidate.getAntecedent().clone();
            subset.remove(positionToRemove);

            boolean found = levelK_1.stream().filter(rule -> rule.getAntecedent().containsAll(subset)).findFirst()
                    .isPresent();

            if (!found)
                return false;
        }

        return true;
    }
}
