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
package lac.algorithms.cba2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lac.algorithms.cba.Rule;
import lac.data.Dataset;

/**
 * Main for the Apriori algorithm. This algorithm has been modified as follows:
 * <ul>
 * <li>Obtains rules directly, without need for obtaining patterns.</li>
 * <li>Has a minimum support for each class.</li>
 * </ul>
 */
public class Apriori {
    /**
     * Configuration used while generating rules
     */
    private Config config;

    /**
     * Dataset used to generate rules
     */
    private Dataset dataset;

    /**
     * Obtained rules
     */
    private ArrayList<Rule> rules;

    /**
     * Support by each class
     */
    private HashMap<Short, Long> supportByKlass;

    /**
     * Default constructor
     * 
     * @param dataset used to obtain class association rules
     * @param config  used to obtain rules
     */
    public Apriori(Dataset dataset, Config config) {
        this.dataset = dataset;

        this.config = config;

        this.supportByKlass = new HashMap<>();
        for (Entry<Short, Long> entry : this.dataset.getFrequencyByKlass().entrySet()) {

            Long minSupport = (long) Math.ceil(this.config.getMinSup() * (double) entry.getValue());
            this.supportByKlass.put(entry.getKey(), minSupport);
        }
    }

    /**
     * Run the algorithm
     * 
     * @return the list of rules
     */
    public ArrayList<Rule> run() {
        rules = new ArrayList<Rule>();

        Map<Short, Long> mapItemCount = new HashMap<Short, Long>();

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

        // we start looking for itemset of size 1
        int k = 1;

        long minSupRelative = Collections.min(supportByKlass.entrySet(), Comparator.comparingLong(Entry::getValue))
                .getValue();

        // We add all frequent items to the set of candidate of size 1
        List<Short> frequent1 = new ArrayList<Short>();
        for (Entry<Short, Long> entry : mapItemCount.entrySet()) {
            if (entry.getValue() >= minSupRelative) {
                frequent1.add(entry.getKey());
            }
        }
        mapItemCount = null;

        // Sort the list of candidates of size 1 by lexical order
        Collections.sort(frequent1, new Comparator<Short>() {
            public int compare(Short o1, Short o2) {
                return o1 - o2;
            }
        });

        // If no frequent item, no rule could be found
        if (frequent1.isEmpty()) {
            return new ArrayList<Rule>();
        }

        // Start generating rules of size 2
        ArrayList<Rule> level = null;
        k = 2;
        do {
            ArrayList<Rule> candidatesK;

            // At level k=2, an optimization to generate candidates is used
            if (k == 2) {
                candidatesK = generateCandidate2(frequent1);
            } else {
                candidatesK = generateCandidateSizeK(level);
            }

            // Calculate supports for candidate rules
            for (Rule candidate : candidatesK) {
                candidate.calculateSupports(dataset);
            }

            // Generate rules for candidate of size k+1
            level = new ArrayList<Rule>();
            for (Rule candidate : candidatesK) {
                if (candidate.getSupportRule() >= this.supportByKlass.get(candidate.getKlass())
                        && !level.contains(candidate)) {
                    // add the candidate
                    level.add(candidate);

                    if (candidate.getConfidence() >= this.config.getMinConf()) {
                        rules.add(candidate);
                    }
                }
            }

            // Cotinue searching for larger rules
            k++;
        } while (!level.isEmpty());

        return rules;
    }

    /**
     * Generate candidate rules of size 2 in antecedent
     * 
     * @param frequent1 singletons used to generate rules
     * @return array of rules being candidates
     */
    private ArrayList<Rule> generateCandidate2(List<Short> frequent1) {
        ArrayList<Rule> candidates = new ArrayList<Rule>();

        for (int i = 0; i < frequent1.size(); i++) {
            Short item1 = frequent1.get(i);

            Rule rule = new Rule();
            rule.add(item1);

            // Combine current antecedent for all the possible classes
            for (int j = 0; j < dataset.getNumberKlasses(); j++) {
                short klass = dataset.getKlass(j);
                rule.setKlass(klass);
                candidates.add((Rule) rule.clone());
            }
        }

        return candidates;
    }

    /**
     * Generate candidate rules of size k using candidates of level k-1
     * 
     * @param levelK_1 rules from previous level
     * @return candidate rules at level K
     */
    protected ArrayList<Rule> generateCandidateSizeK(ArrayList<Rule> levelK_1) {
        ArrayList<Rule> candidates = new ArrayList<Rule>();

        for (int i = 0; i < levelK_1.size(); i++) {
            Rule rule1 = levelK_1.get(i);
            for (int j = i + 1; j < levelK_1.size(); j++) {
                Rule rule2 = levelK_1.get(j);

                if (!isCombinable(rule1, rule2))
                    continue;

                // Create a new candidate by combining itemset1 and itemset2
                Rule newRule = (Rule) rule1.clone();
                newRule.add((rule2.get(rule2.size() - 1)));

                // Apply the anti-monotone property
                if (areSubsetsFrequent(newRule, levelK_1)) {
                    candidates.add(newRule);
                }
            }
        }

        return candidates;
    }

    /**
     * Check if two rules are combinable. They share all the elements except the
     * last item in the antecedent
     * 
     * @param ruleI first rule
     * @param ruleJ second rule
     * @return true if both rules are combinable, false otherwise
     */
    protected boolean isCombinable(Rule ruleI, Rule ruleJ) {
        Short itemi, itemj;

        if (ruleI.getKlass() != ruleJ.getKlass())
            return false;

        if (ruleI.size() != ruleJ.size())
            return false;

        for (int i = 0; i < ruleI.size() - 1; i++) {
            itemi = ruleI.get(i);
            itemj = ruleJ.get(i);

            if (itemi != itemj)
                return false;
        }

        itemi = ruleI.get(ruleI.size() - 1);
        itemj = ruleJ.get(ruleJ.size() - 1);

        if (itemi >= itemj)
            return false;

        return true;
    }

    /**
     * Apply the anti-monotone property, where a candidate of level k must to have
     * all subsets of size k-1 as frequents
     * 
     * @param candidate being evaluated
     * @param levelK_1  rules of previous level
     * @return true if current candidate has all subsets as frequent, false
     *         otherwise
     */
    @SuppressWarnings("unchecked")
    protected boolean areSubsetsFrequent(Rule candidate, List<Rule> levelK_1) {
        for (int posRemoved = 0; posRemoved < candidate.getAntecedent().size(); posRemoved++) {
            ArrayList<Short> subset = (ArrayList<Short>) candidate.getAntecedent().clone();
            subset.remove(posRemoved);

            boolean found = levelK_1.stream().filter(rule -> rule.getAntecedent().containsAll(subset)).findFirst()
                    .isPresent();

            if (!found) {
                return false;
            }
        }

        return true;
    }
}
