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
package lac.algorithms.acn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import lac.data.Dataset;

/**
 * Main class for the ACN classifier. It implements its own way of predicting
 * unseen examples
 * <ul>
 * <li>Precedence of rules. Sorting is required.
 * <li>By confidence</li>
 * <li>By pearson</li>
 * <li>By support of the rule</li>
 * <li>If it is a negative rule has less precedence than positive</li>
 * <li>Size of the antecedent</li>
 * </ul>
 * </li>
 * <li>ACN builds a classifier based on database coverage similar to CBA. ACN
 * takes each rule according to the sorted order and tests if it can provide
 * correct classification for at least one remaining training example. If it
 * can, ACN checks to see if it is a positive or negative rule. If it is a
 * positive rule,it is immediately taken in the final classifier.</li>
 * <li>On the other hand, if it is a negative rule, ACN calculates the accuracy
 * of the rule on the examples remaining. This rule is taken in the final
 * classifier only if the accuracy on the remaining examples is beyond a
 * user-defined threshold. In this way, ACN proceeds until all rules have been
 * examined or all examples have been covered. In case database is uncovered,
 * the default rule is the majority class from uncovered examples. Otherwise it
 * is simply the majority class from the entire training set</li>
 * </ul>
 *
 */
public class Classifier extends lac.algorithms.Classifier {

    /**
     * Build the classifier
     *
     * @param rules    forming the classifier
     * @param training dataset used to train the classifier
     * @param config   for generating the classifier
     */
    public Classifier(ArrayList<Rule> rules, Dataset training, Config config) {
        Collections.sort(rules, new Comparator<Rule>() {
            public int compare(Rule arg0, Rule arg1) {
                if (Double.compare(arg0.getConfidence(), arg1.getConfidence()) != 0) {
                    return -Double.compare(arg0.getConfidence(), arg1.getConfidence());
                } else if (Double.compare(arg0.getPearson(), arg1.getPearson()) != 0) {
                    return -Double.compare(arg0.getPearson(), arg1.getPearson());
                } else if (Double.compare(arg0.getSupportRule(), arg1.getSupportRule()) != 0) {
                    return -Double.compare(arg0.getSupportRule(), arg1.getSupportRule());
                } else if (Integer.compare(arg0.getNegativeItems(), arg1.getNegativeItems()) != 0) {
                    return -Double.compare(arg0.getNegativeItems(), arg1.getNegativeItems());
                } else {
                    return -Integer.compare(arg0.size(), arg1.size());
                }
            }
        });

        ArrayList<Integer> instancesCoveredByRule = new ArrayList<Integer>();

        ArrayList<Boolean> coveredInstances = new ArrayList<Boolean>(Arrays.asList(new Boolean[training.size()]));
        Collections.fill(coveredInstances, Boolean.FALSE);

        for (int i = 0; i < rules.size()
                && coveredInstances.stream().filter(p -> p == false).findFirst().isPresent(); i++) {
            Rule rule = rules.get(i);

            // Check if cover at least one instance
            for (int j = 0; j < training.size(); j++) {
                if (coveredInstances.get(j))
                    continue;

                if (rule.matching(training.getInstance(j).asNominal())) {
                    instancesCoveredByRule.add(j);
                }
            }

            if (instancesCoveredByRule.isEmpty())
                continue;

            if (!rule.isNegative()
                    || this.getAccurracyRemainingDataset(rule, training, coveredInstances) >= config.getMinAcc()) {
                this.rules.add(rule);

                // Remove covered instances
                for (int m = 0; m < instancesCoveredByRule.size(); m++) {
                    int indexInstance = instancesCoveredByRule.get(m);
                    coveredInstances.set(indexInstance, true);
                }
            }
        }

        // Check if there are instances not covered yet
        HashMap<Short, Long> counterByKlass = new HashMap<Short, Long>();
        for (int i = 0; i < training.size(); i++) {
            if (!coveredInstances.get(i)) {
                short klass = training.getKlassInstance(i);

                Long count = counterByKlass.getOrDefault(klass, 0L);
                counterByKlass.put(klass, count + 1L);
            }
        }

        short defaultKlass;
        if (counterByKlass.isEmpty()) {
            // Get majority for the whole dataset
            defaultKlass = Collections.max(training.getFrequencyByKlass().entrySet(),
                    (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
        } else {
            // Get majority for the remaining dataset
            defaultKlass = Collections
                    .max(counterByKlass.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                    .getKey();
        }

        // Remove rules by confidence
        this.rules.removeIf(rule -> rule.getConfidence() < config.getMinConf());

        // Remove rules by pearson coefficient
        this.rules.removeIf(rule -> ((Rule) rule).getPearson() < config.getMinCorr());

        // Default klass, rule without antecedent
        this.rules.add(new Rule(defaultKlass));
    }

    private double getAccurracyRemainingDataset(Rule rule, Dataset dataset, ArrayList<Boolean> coveredInstances) {
        double accuracy = 0;
        int numberNotCoveredInstances = 0;

        for (int i = 0; i < dataset.size(); i++) {
            if (coveredInstances.get(i))
                continue;

            if (rule.matching(dataset.getInstance(i).asNominal()) && rule.getKlass() == dataset.getKlassInstance(i)) {
                accuracy += 1;
            }
            numberNotCoveredInstances += 1;
        }

        return accuracy / numberNotCoveredInstances;
    }
}
