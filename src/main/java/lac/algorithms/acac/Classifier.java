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
package lac.algorithms.acac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lac.data.Instance;

/**
 * Main class for the ACAC classifier. It implements its own way of predicting
 * unseen examples. ACAC collects the subset of rules matching the new object
 * from the set of rules. If all the rules matching the new object have the same
 * class label, ACAC just simply assigns that label to the new object. If the
 * rules are not consistent in class labels, ACAC divides the rules into groups
 * according to class labels. All rules in a group share the same class label
 * and each group has a distinct label.
 *
 * <ul>
 * <li>Firstly, entropy information of a rule’s condset X is used to evaluate
 * its classification power. Laplace expected error estimate is used to estimate
 * this probability.</li>
 *
 * <li>Secondly, the combined effect of a group rule is measured by calculating
 * strength of a rule. The strength of a group combines the average information
 * entropy with the number of rules in the group. The information entropy
 * contribution is larger, so ACAC gives a high weight to it.</li>
 *
 * <li>Finally, ACAC assigns the class label of the group with maximum strength
 * to the new object.</li>
 * </ul>
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Default constructor
     * 
     * @param rules forming the classifier
     */
    public Classifier(ArrayList<Rule> rules) {
        for (int i = 0; i < rules.size(); i++) {
            this.rules.add(rules.get(i));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Classifier#predict(lac.data.Instance)
     */
    @Override
    public short predict(Instance rawInstance) {
        Short[] instance = rawInstance.asNominal();

        List<Rule> firedRules = new ArrayList<Rule>();

        // select only fired rules for this instance
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = (Rule) rules.get(i);

            if (rule.matching(instance)) {
                firedRules.add(rule);
            }
        }

        // When no rule is fired, classifier is not able to perform a prediction
        if (firedRules.isEmpty())
            return NO_PREDICTION;

        Map<Short, Double> strengths = new HashMap<Short, Double>();
        Map<Short, List<Rule>> rulesPerKlass = new HashMap<Short, List<Rule>>();

        Short klass = firedRules.get(0).getKlass();
        for (Rule firedRule : firedRules) {
            // Calculate strength of firedRules by klass
            if (!strengths.containsKey(firedRule.getKlass())) {
                strengths.put(firedRule.getKlass(), 0.0);
                rulesPerKlass.put(firedRule.getKlass(), new ArrayList<Rule>());
            }

            rulesPerKlass.get(firedRule.getKlass()).add(firedRule);
        }

        // When all the firedRules have the same class, we use directly that
        if (rulesPerKlass.size() == 1)
            return klass;

        double nTot = firedRules.size();
        double numberKlasses = rulesPerKlass.keySet().size();

        // Calculate information gain for each group of rule per class
        for (Entry<Short, List<Rule>> entry : rulesPerKlass.entrySet()) {
            klass = entry.getKey();
            List<Rule> rules = entry.getValue();
            double n = rules.size();

            double supAcc = 0.0;
            double numerator = 0.0;

            for (Rule rule : rules) {
                double informationGain = -1.0 / (Math.log(numberKlasses) / Math.log(2.0));

                if (Double.isInfinite(informationGain))
                    informationGain = 0.0;

                for (Entry<Short, List<Rule>> perKlass : rulesPerKlass.entrySet()) {
                    short klass_i = perKlass.getKey();

                    Double ruleSup = (double) rule.getSupportByKlass(klass_i);
                    Double condsup = (double) rule.getSupportAntecedent();

                    Double P_ci_x = (ruleSup + 1) / (condsup + numberKlasses);

                    informationGain += P_ci_x * (Math.log(P_ci_x) / Math.log(2.0));
                }
                supAcc += rule.getSupportAntecedent();

                numerator += rule.getSupportAntecedent() * informationGain;

            }
            double strenght = 0.9 * (1.0 - numerator / supAcc) + 0.1 * n / nTot;

            strengths.put(klass, strenght);
        }

        // Get the class with the highest value of information gain
        return Collections.max(strengths.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .getKey();
    }
}
