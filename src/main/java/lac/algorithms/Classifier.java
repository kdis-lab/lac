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
package lac.algorithms;

import java.util.ArrayList;

import lac.data.Instance;

/**
 * Base class used to represent associative classifiers formed by a set of rules
 */
public class Classifier {
    /**
     * Value used when the classifier is not able to perform a prediction
     */
    public static short NO_PREDICTION = -1;

    /**
     * Array of rules forming the classifier
     */
    protected ArrayList<Rule> rules;

    /**
     * Main constructor
     */
    public Classifier() {
        this.rules = new ArrayList<Rule>();
    }

    /**
     * Add a new rule to the classifier
     * 
     * @param rule to be added in the classifier
     */
    public void add(Rule rule) {
        this.rules.add(rule);
    }

    /**
     * Return the rules forming the classifier
     * 
     * @return rules forming the classifier
     */
    public ArrayList<Rule> getRules() {
        return this.rules;
    }

    /**
     * Performs a prediction on a instance. By default it iterates each rule and
     * assign the first fired rules class
     * 
     * @param rawExample instance to perform prediction
     * @return the assigned class using the current classifier
     */
    public short predict(Instance rawExample) {
        Short[] example = rawExample.asNominal();

        // Check if some rule matchs
        for (int i = 0; i < this.rules.size(); i++) {
            Rule rule = this.rules.get(i);

            if (rule.matching(example))
                return rule.getKlass();
        }

        // No rule was fired
        return NO_PREDICTION;
    }

    /**
     * Number of rules forming the classifier
     * 
     * @return number of rules
     */
    public int getNumberRules() {
        return this.rules.size();
    }
}
