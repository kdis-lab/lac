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
package lac.algorithms.cpar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import lac.data.Instance;

/**
 * Given a rule set containing rules for each class, CPAR uses the best k rules
 * of each class for prediction, with the following procedure: (1) select all
 * the rules whose bodies are satisfied by the example; (2) from the rules
 * selected in step (1), select the best k rules for each class; and (3) compare
 * the average expected accuracy of the best k rules of each class and choose
 * the class with the highest expected accuracy as the predicted class.
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Configuration to generate the classifier
     */
    private Config config;

    /**
     * Constructor
     * 
     * @param rules  candidates to be used in the final classifier
     * @param config used to generate the final classifier
     */
    public Classifier(ArrayList<Rule> rules, Config config) {
        super();

        this.config = config;

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            this.rules.add(rule);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Classifier#predict(lac.data.Instance)
     */
    @Override
    public short predict(Instance rawExample) {
        Short[] example = rawExample.asNominal();
        HashMap<Short, ArrayList<Rule>> firedRules = this.obtainallRulesForRecord(example);

        if (firedRules.isEmpty())
            return NO_PREDICTION;

        // Keep only the best k rules per klass
        firedRules = this.keepBestKrulesPerKlass(firedRules);

        // Select the best group
        return this.selectKlassWithBestAverage(firedRules);

    }

    /**
     * Obtains all the rules which are fired for this example
     * 
     * @param example to search which rule are fired
     * @return the rules fired by klass
     */
    private HashMap<Short, ArrayList<Rule>> obtainallRulesForRecord(Short[] example) {
        HashMap<Short, ArrayList<Rule>> rulesByKlass = new HashMap<Short, ArrayList<Rule>>();

        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = (Rule) this.rules.get(i);

            if (rule.matching(example)) {
                if (!rulesByKlass.containsKey(rule.getKlass())) {
                    rulesByKlass.put(rule.getKlass(), new ArrayList<Rule>());
                }
                rulesByKlass.get(rule.getKlass()).add(rule);
            }
        }

        return rulesByKlass;
    }

    /**
     * Keep only best K rules for each class. Rules are sorted according to laplace
     * accuracy
     * 
     * @param rulesByKlass best rule by klass
     * @return the group of best k rule by klass
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private HashMap<Short, ArrayList<Rule>> keepBestKrulesPerKlass(HashMap<Short, ArrayList<Rule>> rulesByKlass) {
        HashMap<Short, ArrayList<Rule>> kBestRules = new HashMap<Short, ArrayList<Rule>>();

        for (Entry<Short, ArrayList<Rule>> entry : rulesByKlass.entrySet()) {
            entry.getValue().sort(Comparator.comparingDouble(rule -> ((Rule) rule).getLaplace()));

            int numberRules = entry.getValue().size() > config.getK() ? config.getK() : entry.getValue().size();
            ArrayList<Rule> newRules = new ArrayList(entry.getValue().subList(0, numberRules));

            kBestRules.put(entry.getKey(), newRules);
        }

        return kBestRules;
    }

    /**
     * Select the class with the best average of laplace accuracy
     * 
     * @param firedRules k best rules by each class
     * @return the final class
     */
    private short selectKlassWithBestAverage(HashMap<Short, ArrayList<Rule>> firedRules) {
        HashMap<Short, Double> averages = new HashMap<Short, Double>();
        HashMap<Short, Integer> totals = new HashMap<Short, Integer>();

        for (Entry<Short, ArrayList<Rule>> entry : firedRules.entrySet()) {
            if (!averages.containsKey(entry.getKey())) {
                averages.put(entry.getKey(), 0.0);
                totals.put(entry.getKey(), 0);
            }

            for (int i = 0; i < entry.getValue().size(); i++) {
                Rule rule = entry.getValue().get(i);

                averages.put(entry.getKey(), averages.get(entry.getKey()) + rule.getLaplace());
                totals.put(entry.getKey(), totals.get(entry.getKey()) + 1);
            }
        }

        // Determine averages
        for (Entry<Short, ArrayList<Rule>> entry : firedRules.entrySet()) {
            averages.put(entry.getKey(), averages.get(entry.getKey()) / totals.get(entry.getKey()));
        }

        // Get the class with the best average
        return Collections.max(averages.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .getKey();

    }
}
