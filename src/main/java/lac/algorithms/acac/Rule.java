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

import java.util.*;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Base class for a rule of ACAC. It extends base Rule, but add functionalities
 * to calculate all-confidence and informationGain
 */
public class Rule extends lac.algorithms.Rule {

    /**
     * Maximum support for each items. It has to be saved to be able to calculate
     * all-confidence
     */
    private long supportMax;

    /**
     * Support per klass, used to calculate information gain while performin a
     * prediction
     */
    private HashMap<Short, Long> supportRuleByKlass;

    /**
     * Default Constructor
     */
    public Rule() {
        super();
        this.supportRuleByKlass = new HashMap<Short, Long>();
    }

    /**
     * Parameters Constructor
     * 
     * @param klass consequent of the rule
     */
    public Rule(short klass) {
        super(klass);
        this.supportRuleByKlass = new HashMap<Short, Long>();
    }

    /**
     * Constructor for all the parameters forming the rule
     * 
     * @param antecedent        itemset forming the antecedent of the rule
     * @param supportAntecedent frequency of occurence for only the antecedent
     * @param klass             consequent of the rule
     * @param supportRule       frequency of occurrence for the whole rule
     */
    public Rule(short[] antecedent, long supportAntecedent, short klass, long supportRule) {
        this.antecedent = new ArrayList<Short>();
        for (int i = 0; i < antecedent.length; i++) {
            this.antecedent.add(antecedent[i]);
        }
        this.supportAntecedent = supportAntecedent;
        this.supportRule = supportRule;
        this.klass = klass;
        this.supportRuleByKlass = new HashMap<Short, Long>();
    }

    /**
     * It computes the support, supportKlass, and support for each class contained
     * in the dataset
     * 
     * @param train Given training dataset to be able to calculate supports
     */
    public void evaluate(Dataset train) {
        this.supportAntecedent = 0;
        this.supportRule = 0;
        this.supportKlass = 0;

        for (int i = 0; i < train.size(); i++) {
            Short[] example = train.getInstance(i).asNominal();

            Boolean matchAntecedent = Utils.isSubset(antecedent, example);

            Boolean matchConsequent = train.getKlassInstance(i) == this.klass;

            if (matchConsequent) {
                this.supportKlass++;
            }

            if (matchAntecedent) {
                this.supportAntecedent++;
            }

            if (matchAntecedent && matchConsequent) {
                this.supportRule++;
                Long count = this.supportRuleByKlass.getOrDefault(train.getKlassInstance(i), 0L);

                this.supportRuleByKlass.put(train.getKlassInstance(i), count + 1);
            }
        }
    }

    /**
     * Clone function.
     * 
     * @return A copy of the Itemset object.
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        Rule cloned = new Rule(this.klass);
        Short[] newAntecedent = new Short[this.antecedent.size()];
        newAntecedent = this.antecedent.toArray(newAntecedent);
        cloned.add(newAntecedent);

        cloned.supportAntecedent = this.supportAntecedent;
        cloned.supportKlass = this.supportKlass;
        cloned.supportRule = this.supportRule;
        cloned.supportMax = this.supportMax;
        cloned.supportRuleByKlass = (HashMap<Short, Long>) this.supportRuleByKlass.clone();

        return cloned;
    }

    /**
     * Get all-confidence metric for the current rule
     * 
     * @return the value for this metric
     */
    public double getAllConfidence() {
        if (this.getAntecedent().size() == 1)
            return 1.0;
        if (this.supportMax <= 0)
            return Double.NaN;

        return this.supportRule / (double) this.supportMax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#toString()
     */
    public String toString() {
        return super.toString() + " AllConf: " + this.getAllConfidence();
    }

    /**
     * When this rule is generated, it is generated by the combinations of two rules
     * of size k-1. Its all-confidence will use the maximum value of support from
     * those two parents
     * 
     * @param rule1 first parent of size k-1
     * @param rule2 second parent of size k-1
     */
    public void setMaximums(Long rule1, Long rule2) {
        this.supportMax = Long.max(rule1, rule2);
    }

    /**
     * Get support for the class specified as parameter. It will be used to
     * calculate informationGain on the classifier.
     * 
     * @param klass to obtain its support
     * @return the support for the class
     */
    public long getSupportByKlass(short klass) {
        return this.supportRuleByKlass.getOrDefault(klass, 0L);
    }
}
