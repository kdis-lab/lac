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

import java.util.*;
import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Base class for a rule of ACN. It extends base Rule, but add functionalities
 * to negate items
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Determines which items are negated in the antecedent
     */
    private ArrayList<Boolean> negatedItems;

    /**
     * Value for the pearson coeffient for the current rule
     */
    private double pearson;

    /**
     * Default Constructor.
     */
    public Rule() {
        super();
        pearson = Double.NaN;
        this.negatedItems = new ArrayList<Boolean>();
    }

    /**
     * Parameterized Constructor.
     * 
     * @param klass Associated output of the rule
     */
    public Rule(short klass) {
        super(klass);
        this.negatedItems = new ArrayList<Boolean>();
    }

    /**
     * It computes the supports and the pearson coefficient for the current rule.
     * 
     * @param train Given training dataset to be able to calculate supports
     */
    public void evaluate(Dataset train) {
        this.supportAntecedent = 0;
        this.supportRule = 0;
        this.supportKlass = 0;

        for (int i = 0; i < train.size(); i++) {
            Short[] example = train.getInstance(i).asNominal();

            Boolean matchAntecedent = matching(example);

            Boolean matchConsequent = train.getKlassInstance(i) == this.klass;

            if (matchConsequent) {
                this.supportKlass++;
            }

            if (matchAntecedent) {
                this.supportAntecedent++;
            }

            if (matchAntecedent && matchConsequent)
                this.supportRule++;
        }

        double supR = supportRule / ((double) train.size());
        double supA = supportAntecedent / ((double) train.size());
        double supK = supportKlass / ((double) train.size());
        double notSupA = 1.0 - supA;
        double notSupK = 1.0 - supK;
        pearson = (supR - supA * supK) / Math.sqrt(supA * supK * notSupA * notSupK);
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#add(short[])
     */
    @Override
    public void add(short[] item) {
        super.add(item);

        this.negatedItems = new ArrayList<Boolean>();
        for (int i = 0; i < item.length; i++)
            this.negatedItems.add(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#add(short)
     */
    @Override
    public void add(short item) {
        super.add(item);
        this.negatedItems.add(false);
    }

    /**
     * Clone function.
     * 
     * @return A copy of the Rule object.
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
        cloned.negatedItems = (ArrayList<Boolean>) this.negatedItems.clone();

        return cloned;
    }

    /**
     * Negates an item contained in the antecedent of the rule
     * 
     * @param index of the item being negated
     */
    public void negateItem(int index) {
        this.negatedItems.set(index, true);
    }

    /**
     * Pearson coeffient for this rule
     * 
     * @return he coefficient value for this rule
     */
    public double getPearson() {
        return pearson;
    }

    /**
     * Count the number of negated items in the rule
     * 
     * @return number of negated items in the rule
     */
    public int getNegativeItems() {
        return (int) this.negatedItems.stream().filter(p -> p == true).count();
    }

    /**
     * Check if it has some negated items in the rule
     * 
     * @return true, if some item is negated, false otherwise
     */
    public boolean isNegative() {
        return this.getNegativeItems() > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#matching(java.lang.Short[])
     */
    @Override
    public boolean matching(Short[] example) {
        if (antecedent.isEmpty())
            return true;

        if (this.isNegative()) {
            ArrayList<Short> positiveAntecedent = new ArrayList<Short>();

            for (int i = 0; i < this.antecedent.size(); i++) {
                if (this.negatedItems.get(i)) {
                    short negativeItem = this.antecedent.get(i);

                    // If contain negative Item, it cannot match this example
                    if (antecedent.contains(negativeItem))
                        return false;
                } else {
                    positiveAntecedent.add(this.antecedent.get(i));
                }
            }

            return Utils.isSubset(positiveAntecedent, example);
        } else {
            return super.matching(example);
        }
    }
}
