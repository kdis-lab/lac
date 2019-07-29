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
package lac.algorithms.cmar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Dataset;
import lac.data.Instance;

/**
 * Generates classifier by means of the previously obtained rules. It sorts the
 * rules by confidence, support and size. Then, general rules are only
 * considered removing low confidence and no general rules. Finally, only
 * positively correlated rules are considered
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Constructor
     * 
     * @param rules    forming the current classifier
     * @param training dataset used while training classifier
     * @param config   configuration used to generate classifier
     * 
     * @throws Exception
     */
    public Classifier(ArrayList<lac.algorithms.Rule> rules, Dataset training, Config config) throws Exception {
        super();
        lac.algorithms.cmar.Rule.NUMBER_INSTANCES = training.size();
        CRTree.NUMBER_SINGLETONS = training.getNumberSingletons();
        CRTree crTree = new CRTree(training, (Config) config);
        for (lac.algorithms.Rule rule : rules) {
            crTree.insert(rule);
        }
        crTree.pruneUsingCover();

        this.rules = crTree.getRules();
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Classifier#predict(lac.data.Instance)
     */
    @Override
    public short predict(Instance rawExample) {
        Short[] example = rawExample.asNominal();

        ArrayList<Rule> matchingRules = obtainallRulesForRecord(example);

        // If no rules satisfy record, it cannot be performed any prediction
        if (matchingRules.isEmpty()) {
            return NO_PREDICTION;
        }

        // If only one rule return class
        if (matchingRules.size() == 1) {
            return (short) (matchingRules.get(0).getKlass());
        }

        // If more than one rule but all have the same class return calss
        if (onlyOneClass(matchingRules)) {
            return (short) (matchingRules.get(0).getKlass());
        }

        // Group rules
        HashMap<Short, ArrayList<Rule>> ruleGroups = groupRulesByKlass(matchingRules);

        // Weighted Chi-Squared (WCS) Values for each group
        HashMap<Short, Double> wcsValues = calculateChiSquaredValues(ruleGroups);

        // Select group with best WCS value and return associated class
        return (short) (Collections.max(wcsValues.entrySet(), Comparator.comparingDouble(Entry::getValue)).getKey());
    }

    /**
     * Forms groups of rules in function of its consequent
     * 
     * @param rules to be grouped by consequent
     * @return group of rules by klass
     */
    private HashMap<Short, ArrayList<Rule>> groupRulesByKlass(ArrayList<Rule> rules) {
        HashMap<Short, ArrayList<Rule>> rulesByGroup = new HashMap<Short, ArrayList<Rule>>();

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);

            if (!rulesByGroup.containsKey(rule.getKlass()))
                rulesByGroup.put(rule.getKlass(), new ArrayList<Rule>());

            rulesByGroup.get(rule.getKlass()).add(rule);
        }

        return rulesByGroup;
    }

    /**
     * Check if in specified rules there are more than one class
     * 
     * @param rules to check if they have more class
     * @return true if there are only one class, false otherwise
     */
    private boolean onlyOneClass(ArrayList<Rule> rules) {
        short firstKlass = rules.get(0).getKlass();

        for (int i = 1; i < rules.size(); i++) {
            if (rules.get(i).getKlass() != firstKlass)
                return false;
        }

        return true;
    }

    /**
     * Determines and returns the weighted Chi Squared values for the groups of
     * rules.
     * 
     * @param ruleByGroup the given groups of rule.
     * @return array of weighted Chi-Squared value for a set of rule groups
     */
    private HashMap<Short, Double> calculateChiSquaredValues(HashMap<Short, ArrayList<Rule>> rulesByGroup) {
        HashMap<Short, Double> chiSquaredByKlass = new HashMap<Short, Double>();

        for (Entry<Short, ArrayList<Rule>> entry : rulesByGroup.entrySet()) {
            double wcsValue = 0.0;

            for (int i = 0; i < entry.getValue().size(); i++) {
                Rule rule = entry.getValue().get(i);

                double chiSquare = rule.getChiSquare();
                double chiSquareUB = rule.getChiSquareUpperBound();

                wcsValue += (chiSquare * chiSquare) / chiSquareUB;
            }

            chiSquaredByKlass.put(entry.getKey(), wcsValue);
        }

        return chiSquaredByKlass;
    }

    /**
     * Obtains all rules which are fired for current example
     * 
     * @param example to check rules
     * @return list of rules fired with current example
     */
    private ArrayList<Rule> obtainallRulesForRecord(Short[] example) {
        ArrayList<Rule> result = new ArrayList<Rule>();

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = (Rule) rules.get(i);

            if (rule.matching(example)) {
                result.add(rule);
            }
        }
        return result;
    }
}
