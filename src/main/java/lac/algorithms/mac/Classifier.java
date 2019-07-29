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
package lac.algorithms.mac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lac.data.Dataset;
import lac.data.Instance;
import lac.utils.Utils;

/**
 * Main class for the MAC classifier. It implements its own way of predicting
 * unseen examples. MAC collects the subset of rules matching the new object
 * from the set of rules. If all the rules matching the new object have the same
 * class label, MAC just simply assigns that label to the new object. If the
 * rules are not consistent in class labels, MAC assigns the klass with the
 * highest number of rules
 * 
 * To obtain the final classifier, rule are post-processed following these
 * steps.
 *
 * <ul>
 * <li>Rules are sorted according to confidence, support and size.</li>
 * <li>To use a rule in the classifier, it has to cover at least one example
 * from the training dataset, in other case it is discarded</li>
 *
 * <li>Finally, the majority class is selected. At this point there are two
 * possibilites:
 * <ul>
 * <li>All the dataset have been covered, in this case the majority class is
 * selected for the whole dataset</li>
 * 
 * <li>All the dataset have not been covered yet, in this case the majority
 * class is selected from the remaining instances</li>
 * </ul>
 * </li>
 * </ul>
 */
public class Classifier extends lac.algorithms.Classifier {

    /**
     * Performs a post-processing of the rules to form the final classifier
     * 
     * @param dataset dataset where the rule have been obtained, ie, training
     *                dataset
     * @param rules   set of rules forming the classifier
     */
    public Classifier(Dataset dataset, ArrayList<Rule> rules) {
        super();

        // Sort rules by confidence, support and size
        Collections.sort(rules, new Comparator<Rule>() {
            public int compare(Rule arg0, Rule arg1) {
                if (Double.compare(arg0.getConfidence(), arg1.getConfidence()) != 0) {
                    return -Double.compare(arg0.getConfidence(), arg1.getConfidence());
                } else if (Double.compare(arg0.getSupportRule(), arg1.getSupportRule()) != 0) {
                    return -Double.compare(arg0.getSupportRule(), arg1.getSupportRule());
                } else {
                    return -Integer.compare(arg0.size(), arg1.size());
                }
            }
        });

        // Only those rules whose antecedent at least covver one instance are selected
        ArrayList<Rule> finalRules = new ArrayList<Rule>();
        Boolean[] covered = new Boolean[dataset.size()];
        Arrays.fill(covered, Boolean.FALSE);

        for (int k = 0; k < rules.size(); k++) {
            Rule rule = rules.get(k);

            for (int i = 0; i < dataset.size(); i++) {
                Short[] instance = dataset.getInstance(i).asNominal();

                if (!covered[i] && Utils.isSubset(rule.getAntecedent(), instance)) {
                    covered[i] = true;
                    if (!finalRules.contains(rule))
                        finalRules.add(rule);
                }
            }
        }

        this.rules.clear();
        this.rules.addAll(finalRules);

        // Select majority class, there are two possible scenario
        // 1.- There are instances not yet covered, the majority class for those
        // instances is selected
        // 2.- There are not instances not yet covered, the majority class for the whole
        // dataset
        Boolean allAreCovered = true;
        for (int j = 0; j < covered.length && allAreCovered; j++) {
            if (!covered[j]) {
                allAreCovered = false;
            }
        }

        Map<Short, Long> classesCounter = new HashMap<Short, Long>();
        if (allAreCovered) {
            for (int k = 0; k < this.rules.size(); k++) {
                Rule rule = (Rule) this.rules.get(k);
                Long counter = classesCounter.get(rule.getKlass());

                if (counter == null) {
                    classesCounter.put(rule.getKlass(), 1L);
                } else {
                    classesCounter.put(rule.getKlass(), counter + 1);
                }
            }
        } else {
            for (int i = 0; i < dataset.size(); i++) {
                if (!covered[i]) {
                    short klass = dataset.getKlassInstance(i);
                    Long counter = classesCounter.get(klass);

                    if (counter == null) {
                        classesCounter.put(klass, 1L);
                    } else {
                        classesCounter.put(klass, counter + 1);
                    }
                }
            }
        }

        short defaultKlass = (Short) classesCounter.keySet().toArray()[0];
        Long counter = (Long) classesCounter.values().toArray()[0];

        for (Entry<Short, Long> entry : classesCounter.entrySet()) {
            if (counter < entry.getValue()) {
                counter = entry.getValue();
                defaultKlass = entry.getKey();
            }
        }

        add(new Rule(defaultKlass));
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Classifier#predict(lac.data.Instance)
     */
    @Override
    public short predict(Instance rawExample) {
        Short[] example = rawExample.asNominal();

        HashMap<Short, Long> matchPerKlass = new HashMap<Short, Long>();
        short defaultKlass = -1;

        // We need to count the number of rules which were fired in function of the
        // klass, the final
        // classification will be the klass with more number of rules
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = (Rule) this.rules.get(i);

            // Rule without antecedent is a special case, and it should not be included in
            // those groups
            if (rule.getAntecedent().isEmpty()) {
                defaultKlass = rule.getKlass();
                continue;
            }

            if (Utils.isSubset(rule.getAntecedent(), example)) {
                Long counter = matchPerKlass.get(rule.getKlass());

                if (counter == null) {
                    matchPerKlass.put(rule.getKlass(), 1L);
                } else {
                    matchPerKlass.put(rule.getKlass(), counter + 1);
                }
            }
        }

        // no rule was fired
        if (matchPerKlass.isEmpty())
            return defaultKlass;

        // Get the klass with the highest number of rules
        return Collections
                .max(matchPerKlass.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .getKey();
    }
}