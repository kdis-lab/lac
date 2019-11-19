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
package lac.algorithms.l3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lac.algorithms.Rule;
import lac.data.Dataset;
import lac.data.Instance;

/**
 * Before performing lazy pruning, a global order is imposed on the rule base .
 * It first sorts by confidence, support, size and lexicography. The difference
 * with regard to pevious approaches is that L3 prefers rules much larger
 * (decreasing order of size). The technique of lazy pruning proposed to discard
 * from the classifier only the rules that do not correctly classify any
 * training case (rules that only negatively contribute to the classification)
 * After sorting, the training cases is used to detect "harmful" rules. For each
 * training case, L3 assigns it to the first rule in the sort order that covers
 * it and checks if the assigned class label was correct or wrong. The case is
 * then discarded. After all cases have been considered, the pruning step
 * discards rules that only classified training cases wrongly . Training cases
 * classified by discarded rules enter again the assignment loop , and the cycle
 * is repeated until no training case is covered by discarded rules. After
 * having discarded "harmful" rules, the remaining rules are divided in two
 * groups: used rules which have already correctly classified at least one
 * training case, spare rules which have not been used during the training
 * phase, but may become useful later. Both groups of rules are used to create
 * the classifier. In particular, used rules are assigned to the first level of
 * the classifier, while spare rules yield the second level.
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Lazily create the classifier
     * 
     * @param training Dataset used to generate the classifier
     * @param rules    which will form the final classifier
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Classifier(Dataset training, ArrayList<Rule> rules) {
        super();

        ArrayList<Rule> lI = new ArrayList();
        ArrayList<Rule> lII = new ArrayList();

        // Sorts rules by confidence, support, size and lexicography order
        ArrayList<Rule> sortedRules = (ArrayList<Rule>) rules.clone();
        Collections.sort(sortedRules, new Comparator<Rule>() {
            public int compare(Rule arg0, Rule arg1) {
                if (Double.compare(arg0.getConfidence(), arg1.getConfidence()) != 0) {
                    return -Double.compare(arg0.getConfidence(), arg1.getConfidence());
                } else if (Double.compare(arg0.getSupportRule(), arg1.getSupportRule()) != 0) {
                    return -Double.compare(arg0.getSupportRule(), arg1.getSupportRule());
                } else if (Integer.compare(arg0.size(), arg1.size()) != 0) {
                    return -Integer.compare(arg0.size(), arg1.size());
                } else {
                    // Lexicography order
                    for (int i = 0; i < arg0.size(); i++) {
                        short x = arg0.getAntecedent().get(i);
                        short y = arg1.getAntecedent().get(i);
                        if (Integer.compare(x, y) != 0)
                            return Integer.compare(x, y);
                    }
                    return Integer.compare(arg0.getKlass(), arg1.getKlass());
                }
            }
        });

        /**
         * Iterate for each rule doing: 1.- Evaluate rule in remaining dataset 2.- If
         * rule correctly classify at least one instance in the remaining dataset, it
         * will be of levelI. If rule doesn't cover correctly any instance, but it
         * doesn't missclassify any one, it will be of levelII Other rules will be
         * discarded 3.- Remove transactions covered from current rule
         * 
         */
        boolean[] instanceCovered = new boolean[training.size()];

        for (Rule rule : sortedRules) {
            int correctly = 0;
            int incorrectly = 0;

            for (int i = 0; i < training.size(); i++) {
                Short[] example = training.getInstance(i).asNominal();

                if (!instanceCovered[i] && rule.matching(example)) {
                    if (rule.getKlass() == training.getKlassInstance(i)) {
                        correctly++;
                        instanceCovered[i] = true;
                    } else {
                        incorrectly++;
                    }
                }
            }

            if (correctly > 0) {
                lI.add(rule);
            } else if (correctly == 0 && incorrectly == 0) {
                lII.add(rule);
            }
        }

        // Add all the instances, in the correct order, first levelI should be used,
        // then levelII
        this.rules = (ArrayList<Rule>) lI.clone();
        this.rules.addAll(lII);
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Classifier#predict(lac.data.Instance)
     */ @Override
    public short predict(Instance rawExample) {
        Short[] example = (Short[]) rawExample.asNominal();
        // Check if some rule matchs
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = this.rules.get(i);

            if (rule.matching(example))
                return (short) (rule.getKlass());
        }

        return NO_PREDICTION;
    }
}
