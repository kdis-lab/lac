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

import lac.utils.Utils;

/**
 * Main class used to represent class association rules
 */
public class Rule implements Cloneable {

    /**
     * Array with all the items forming the antecedent
     */
    protected ArrayList<Short> antecedent;

    /**
     * Consequent for the current rule
     */
    protected short klass;

    /**
     * Frequency of occurrence relative to the dataset for the antecedent
     */
    protected long supportAntecedent;

    /**
     * Frequency of occurrence relative to the dataset for the klass
     */
    protected long supportKlass;

    /**
     * Frequency of occurrence relative to the dataset for the whole rule
     */
    protected long supportRule;

    /**
     * Main constructor
     */
    public Rule() {
        this.antecedent = new ArrayList<Short>();
        this.supportRule = 0;
        this.supportAntecedent = 0;
        this.supportKlass = 0;
    }

    /**
     * Constructor
     * 
     * @param klass consequent of the rule
     */
    public Rule(short klass) {
        this();
        this.klass = klass;
    }

    /**
     * Constructor
     * 
     * @param antecedent antecedent of the rule
     * @param klass      consquent of the rule
     */
    public Rule(short[] antecedent, short klass) {
        this(klass);

        for (int i = 0; i < antecedent.length; i++) {
            this.antecedent.add(antecedent[i]);
        }
    }

    /**
     * Constructor
     * 
     * @param antecedent antecedent of the rule
     * @param klass      consquent of the rule
     */
    public Rule(Short[] antecedent, short klass) {
        this(klass);

        for (int i = 0; i < antecedent.length; i++) {
            this.antecedent.add(antecedent[i]);
        }
    }

    /**
     * Returns the antecedent of the rule
     * 
     * @return antecedent of the rule
     */
    public ArrayList<Short> getAntecedent() {
        return this.antecedent;
    }

    /**
     * Get confidence for the current evaluated rule
     * 
     * @return confidence or 0 if supportAntecedent is 0
     */
    public double getConfidence() {
        double confidence = (double) this.supportRule / (double) this.supportAntecedent;

        return this.supportAntecedent > 0.0 ? confidence : 0.0;
    }

    /**
     * Get support for the whole rule
     * 
     * @return relative support for the current rule
     */
    public long getSupportRule() {
        return this.supportRule;
    }

    /**
     * Get support for the class (consequent)
     * 
     * @return relative support for the consequent
     */
    public long getSupportConsequent() {
        return this.supportKlass;
    }

    /**
     * Function to check if a given example fires a rule
     * 
     * @param example Example to be classified
     * @return true if rule was fired, false otherwise
     */
    public boolean matching(Short[] example) {
        if (antecedent.isEmpty())
            return true;

        return Utils.isSubset(antecedent, example);
    }

    /**
     * Function to check if a rule is equal to another given.
     * 
     * @param rule Rule to compare with current
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        Rule rule = (Rule) o;

        if (this.klass != rule.getKlass())
            return false;

        if (this.antecedent.size() != rule.getAntecedent().size())
            return false;

        for (int i = 0; i < this.antecedent.size(); i++)
            if (this.antecedent.get(i) != rule.antecedent.get(i))
                return false;

        return true;
    }

    /**
     * Add a set of items to the antecedent
     * 
     * @param itemset Element to be added
     */
    public void add(Short[] itemset) {
        for (int i = 0; i < itemset.length; i++)
            this.antecedent.add(itemset[i]);
    }

    /**
     * Add a set of items to the antecedent
     * 
     * @param itemset to be added to the antecedent
     */
    public void add(short[] itemset) {
        for (int i = 0; i < itemset.length; i++)
            this.antecedent.add(itemset[i]);
    }

    /**
     * Add a new item to the antecedent
     * 
     * @param item to be added
     */
    public void add(short item) {
        this.antecedent.add(item);
    }

    /**
     * It returns the item located in the given position of the antecedent
     * 
     * @param index Position of the requested item into the antecedent
     * @return The requested item of the antecedent
     */
    public short get(int index) {
        return this.antecedent.get(index);
    }

    /**
     * It returns the size of the antecedent
     * 
     * @return Number of items in the antecedent
     */
    public int size() {
        return this.antecedent.size();
    }

    /**
     * It returns the consequent (class)
     * 
     * @return short output class
     */
    public short getKlass() {
        return this.klass;
    }

    /**
     * It returns the support of the antecedent
     * 
     * @return support of the antecedent
     */
    public long getSupportAntecedent() {
        return this.supportAntecedent;
    }

    /**
     * Function which sets the rule's klass.
     * 
     * @param klasss rule's klass
     */
    public void setKlass(short klass) {
        this.klass = klass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.antecedent.toString() + " -> " + this.getKlass() + " Sup: " + this.getSupportRule() + " Conf: "
                + this.getConfidence();
    }

    /**
     * Clone function.
     * 
     * @return a clone of the Rule object.
     */
    @Override
    public Object clone() {
        Rule cloned = new Rule(this.klass);

        Short[] newAntecedent = new Short[this.antecedent.size()];
        newAntecedent = this.antecedent.toArray(newAntecedent);
        cloned.add(newAntecedent);

        cloned.supportAntecedent = this.supportAntecedent;
        cloned.supportKlass = this.supportKlass;
        cloned.supportRule = this.supportRule;

        return cloned;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @SuppressWarnings("unchecked")
    public int hashCode() {
        ArrayList<Short> total = (ArrayList<Short>) antecedent.clone();
        total.add(klass);
        return total.hashCode();
    }

}
