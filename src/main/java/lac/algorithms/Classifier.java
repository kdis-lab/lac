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
 * Base class used to represent associative classifiers formed by a set of
 * rules.
 * <a href="https://github.com/kdis-lab/lac/blob/main/doc/manual.pdf">Manual
 * Section 5.3</a> contains a complete example on how to add new classifiers.
 * <ul>
 * <li>In the package of the new algorithm being added, a new class called
 * Classifier has to be created. Its aim should be to store all the obtained
 * rules and to have all the logic required to predict unseen examples. It
 * should extend from lac.algorithms.Classifier</li>
 * <li>Classifier class has to implement two methods. First, a constructor
 * should be implemented, its goal is to initialize the state calling super.
 * Second, a method called predict receiving an array of short and returning a
 * short may be implemented. The aim of this method is to predict the example
 * received as argument by means of the previously extracted rules. It should be
 * highlighted that LAC stores internally each attributes' value as short, in
 * this way much less memory is used. This representation is totally internal,
 * and is managed automatically by lac.data.Dataset. Of course, this
 * representation is also completely transparent for end-user and they do not
 * need to preprocess nor do any kind of process. This technique of saving
 * instances as short and not by the original types (string in almost all the
 * cases) is well-known and it is being used by many current tools for both AC
 * and ARM.</li>
 * </ul>
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
