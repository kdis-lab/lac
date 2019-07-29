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
package lac.algorithms.cba;

import java.util.*;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class used to represent a rule in the CBA algorithm
 */
@SuppressWarnings("rawtypes")
public class Rule extends lac.algorithms.Rule implements Comparable {
    /**
     * Field used to store the pessimistic error rate
     */
    private double pessimisticErrorRate;

    /**
     * Number of times that a rule is fired and it correctly classify a case
     */
    private long hits;

    /**
     * Number of times that a rule is fired but it doesn't correctly classify a case
     */
    private long misses;

    /**
     * Time used to check when it was generated
     */
    private long time;

    /**
     * Flag to determine if a rule has been marked or not
     */
    private Boolean mark = false;

    /**
     * Number of cases covered by klass
     */
    private HashMap<Short, Long> klassesCovered;

    /**
     * Replacement for the current rule
     */
    private ArrayList<Replace> replace;

    /**
     * Used to calculate errors for the current rule
     */
    private double VAL[] = { 0, 0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.00005, 0.0001, 0.0005, 0.001,
            0.005, 0.01, 0.05, 0.10, 0.20, 0.40, 1.00 };
    private double DEV[] = { 100, 6.0, 5.61, 5.2, 4.75, 4.26, 3.89, 3.72, 3.29, 3.09, 2.58, 2.33, 1.65, 1.28, 0.84,
            0.25, 0.00 };

    /**
     * Constructor
     */
    public Rule() {
        super();
        this.klassesCovered = new HashMap<Short, Long>();
        this.replace = new ArrayList<Replace>();
        this.time = System.currentTimeMillis();
    }

    /**
     * Constructor
     * 
     * @param klass consequent for the current rule
     */
    public Rule(short klass) {
        super(klass);
        this.pessimisticErrorRate = 0;
        this.hits = 0;
        this.misses = 0;
    }

    /**
     * Get the number of misses for the current rule
     * 
     * @return number of misses
     */
    public long getMisses() {
        return this.misses;
    }

    /**
     * Returns the Pessimistic Error Rate of the current rule
     * 
     * @return Pessimistic Error Rate for the current rule
     */
    public double getPessimisticErrorRate() {
        return this.pessimisticErrorRate;
    }

    /**
     * Check whether the antecedent of the specified rule is a subset of the current
     * rule
     * 
     * @param a
     * @return
     */
    public boolean isSubset(Rule a) {
        if (this.klass != a.getKlass())
            return false;

        short itemi, itemj;
        for (int i = 0; i < this.antecedent.size(); i++) {
            itemi = this.antecedent.get(i);

            boolean found = false;
            for (int j = 0; j < a.antecedent.size() && !found; j++) {
                itemj = a.antecedent.get(j);
                if (itemi == itemj)
                    found = true;
                else if (itemj >= itemi)
                    return false;
            }

            if (!found)
                return false;
        }

        return true;
    }

    /**
     * Evaluates the current rule for the specified dataset
     * 
     * @param train dataset for evaluating the current rule
     */
    public void calculateSupports(Dataset train) {
        this.supportAntecedent = 0;
        this.supportRule = 0;
        this.supportKlass = 0;

        for (int i = 0; i < train.size(); i++) {
            Short[] example = train.getInstance(i).asNominal();

            Boolean matchAntecedent = Utils.isSubset(antecedent, example);

            Boolean matchConsequent = train.getKlassInstance(i) == this.klass;

            if (matchConsequent) {
                this.supportKlass++;
                this.hits++;
            } else {
                this.misses++;
            }

            if (matchAntecedent) {
                this.supportAntecedent++;
            }

            if (matchAntecedent && matchConsequent)
                this.supportRule++;
        }

        this.pessimisticErrorRate = (1.0 * this.misses
                + this.errors(this.hits + this.misses * 1.0, this.misses * 1.0, 0.25)) / (misses + hits);
        this.time = System.currentTimeMillis();
    }

    /**
     * Calculate the errors produced by current rule
     * 
     * @param N
     * @param e
     * @param CF
     * @return
     */
    private double errors(double N, double e, double CF) {
        double Val0, Pr, Coeff;
        int i;

        Coeff = 0;
        i = 0;

        while (CF > VAL[i])
            i++;

        Coeff = DEV[i - 1] + (DEV[i] - DEV[i - 1]) * (CF - VAL[i - 1]) / (VAL[i] - VAL[i - 1]);
        Coeff = Coeff * Coeff;

        if (e < 1E-6)
            return N * (1.0 - Math.exp(Math.log(CF) / N));
        else {
            if (e < 0.9999) {
                Val0 = N * (1 - Math.exp(Math.log(CF) / N));
                return (Val0 + e * (errors(N, 1.0, CF) - Val0));
            } else {
                if (e + 0.5 >= N)
                    return (0.67 * (N - e));
                else {
                    Pr = (e + 0.5 + Coeff / 2 + Math.sqrt(Coeff * ((e + 0.5) * (1 - (e + 0.5) / N) + Coeff / 4)))
                            / (N + Coeff);
                    return (N * Pr - e);
                }
            }
        }
    }

    /**
     * Add a new replacement to the current rule
     * 
     * @param replace new replacement for the rule
     */
    public void addReplace(Replace replace) {
        this.replace.add(replace);
    }

    /**
     * Mark a rule
     */
    public void mark() {
        this.mark = true;
    }

    /**
     * Increment the number of cases covered for the specified klass
     * 
     * @param klass to increment the number of cases
     */
    public void incrementKlassCovered(short klass) {
        if (this.klassesCovered.containsKey(klass)) {
            this.klassesCovered.put(klass, this.klassesCovered.get(klass) + 1);
        } else {
            this.klassesCovered.put(klass, 1L);
        }
    }

    /**
     * Decrement the cases covered for the specified class
     * 
     * @param klass to decrement counter
     */
    public void decrementKlassCovered(short klass) {
        if (this.klassesCovered.containsKey(klass)) {
            this.klassesCovered.put(klass, this.klassesCovered.get(klass) - 1);
        }
    }

    /**
     * Check if rule is marked
     * 
     * @return true if rule is covered
     */
    public boolean isMark() {
        return mark;
    }

    /**
     * Check whether the specified rule has precedence with regard to current one
     * 
     * @param r rule to check precedence
     * @return true or false, if current rule has precedence
     */
    public boolean isPrecedence(Rule r) {
        if (this.getConfidence() > r.getConfidence())
            return true;
        else if (this.getConfidence() < r.getConfidence())
            return false;

        if (this.getSupportRule() > r.getSupportRule())
            return true;
        else if (this.getSupportRule() < r.getSupportRule())
            return false;

        if (this.getAntecedent().size() < r.getAntecedent().size())
            return true;
        else if (this.getAntecedent().size() > r.getAntecedent().size())
            return false;

        return true;
    }

    /**
     * Get the number of replace rules
     * 
     * @return the number of replacement rules
     */
    public int getNumberReplace() {
        return this.replace.size();
    }

    /**
     * Get the replacement rule at position specified
     * 
     * @param j index to get the replacement
     * @return the new replacement
     */
    public Replace getReplace(int j) {
        return this.replace.get(j);
    }

    /**
     * Get number of cases covered for the specified class
     * 
     * @param klass to check the number of cases
     * @return the number of cases
     */
    public Long getKlassesCovered(short klass) {
        return this.klassesCovered.get(klass);
    }

    /**
     * It determines if two rules could be combined to generate larger rules. They
     * have to satisfy the following conditions:
     * 
     * <ul>
     * <li>Class, that is, consequent is equal.</li>
     * <li>Size of the antecedent is equal.</li>
     * <li>They share all the item except one</li>
     * <li>For the not sharing item, lexicography order is applied</li>
     * </ul>
     * 
     * @param other
     * @return true, if rules are
     */
    protected boolean isCombinable(Rule other) {
        if (this.getKlass() != other.getKlass())
            return false;

        if (this.size() != other.size())
            return false;

        Short itemi, itemj;
        for (int i = 0; i < this.size() - 1; i++) {
            itemi = this.get(i);
            itemj = other.get(i);

            if (itemi != itemj)
                return false;
        }

        itemi = this.get(this.size() - 1);
        itemj = other.get(other.size() - 1);

        if (itemi >= itemj)
            return false;

        return true;
    }

    @Override
    public int compareTo(Object a) {
        if (((Rule) a).getConfidence() < this.getConfidence())
            return -1;
        else if (((Rule) a).getConfidence() > this.getConfidence())
            return 1;

        if (((Rule) a).getSupportRule() < this.getSupportRule())
            return -1;
        else if (((Rule) a).getSupportRule() > this.getSupportRule())
            return 1;

        if (((Rule) a).time < this.time)
            return 1;
        else if (((Rule) a).time > this.time)
            return -1;

        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        Rule cloned = new Rule(this.klass);
        Short[] newAntecedent = new Short[this.antecedent.size()];
        newAntecedent = this.antecedent.toArray(newAntecedent);
        cloned.add(newAntecedent);

        cloned.supportAntecedent = this.supportAntecedent;
        cloned.supportKlass = this.supportKlass;
        cloned.supportRule = this.supportRule;

        cloned.replace = new ArrayList<Replace>();
        for (int i = 0; i < this.replace.size(); i++)
            cloned.replace.add((Replace) (replace.get(i)).clone());

        cloned.klassesCovered = (HashMap<Short, Long>) this.klassesCovered.clone();

        cloned.pessimisticErrorRate = this.pessimisticErrorRate;
        cloned.hits = this.hits;
        cloned.mark = this.mark;
        cloned.misses = this.misses;

        return cloned;
    }
}
