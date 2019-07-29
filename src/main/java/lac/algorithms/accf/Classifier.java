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
package lac.algorithms.accf;

import java.util.Collections;
import java.util.Comparator;

import lac.data.Instance;
import lac.utils.Utils;

import java.util.ArrayList;

/**
 * Main class for the ACCF classifier. It implements its own way of predicting
 * unseen examples
 */
public class Classifier extends lac.algorithms.Classifier {
    /**
     * Default constructor
     * 
     * @param rules array of rules forming trhe classifier
     */
    public Classifier(ArrayList<Rule> rules) {
        Collections.sort(rules, new Comparator<Rule>() {
            @Override
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

        for (int i = 0; i < rules.size(); i++) {
            this.add(rules.get(i));
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

        // Check if some rule matchs
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = (Rule) this.rules.get(i);

            if (rule.matching(example))
                return rule.getKlass();
        }

        // When no rule is fired, we need to find the first rule whose antecedent has at
        // least one matching with the example
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = (Rule) this.rules.get(i);

            if (!Utils.intersect(rule.getAntecedent(), example).isEmpty())
                return rule.getKlass();
        }

        // We don't know how to classify that
        return NO_PREDICTION;
    }

}
