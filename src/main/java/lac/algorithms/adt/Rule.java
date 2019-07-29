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
package lac.algorithms.adt;

import java.util.ArrayList;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class used to represent a Rule in ADT algorithm
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Number of times that an instance is matched, but class is wrong
     */
    private long misses;

    /**
     * Number of times that an instance is matched and class is right
     */
    private long hits;

    /**
     * Instances covered by this rule
     */
    private ArrayList<Integer> coveredInstances;

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      consequent of the rule
     */
    public Rule(Short[] antecedent, short klass) {
        super(antecedent, klass);
        coveredInstances = new ArrayList<Integer>();
        misses = 0;
        hits = 0;
    }

    /**
     * Constructor
     * 
     * @param newAntecedent antecedent of the rule
     * @param klass         consequent of the rule
     */
    @SuppressWarnings("unchecked")
    public Rule(ArrayList<Short> newAntecedent, short klass) {
        super(klass);
        coveredInstances = new ArrayList<Integer>();
        this.antecedent = (ArrayList<Short>) newAntecedent.clone();
        misses = 0;
        hits = 0;
    }

    /**
     * Constructor
     * 
     * @param klass consequent of the rule
     */
    public Rule(short klass) {
        super(klass);
        coveredInstances = new ArrayList<Integer>();
    }

    /**
     * Increment the support for the antecedent
     */
    public void incrementSupportAntecedent() {
        this.supportAntecedent++;
    }

    /**
     * Increment the support for the rule
     */
    public void incrementSupportRule() {
        this.supportRule++;
    }

    /**
     * Add an tid to the array of covered instances
     * 
     * @param tid to be added as covered
     */
    public void addCoveredInstance(Integer tid) {
        this.coveredInstances.add(tid);
    }

    /**
     * Get pessimistic error estimate
     * 
     * @return the pessimistic error estimate
     */
    public double getPessimisticErrorEstimate() {
        return errors(this.hits + this.misses, this.misses) + this.misses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#toString()
     */
    public String toString() {
        return super.toString() + " hits: " + this.hits + " misses: " + this.misses + " per; "
                + this.getPessimisticErrorEstimate();
    }

    /**
     * Calculate supports for the dataset passed as parameter
     * 
     * @param train dataset used as training set
     */
    public void calculateSupports(Dataset train) {
        this.supportAntecedent = 0;
        this.supportRule = 0;

        for (int i = 0; i < train.size(); i++) {
            Short[] example = train.getInstance(i).asNominal();

            Boolean matchAntecedent = Utils.isSubset(antecedent, example);

            Boolean matchConsequent = train.getKlassInstance(i) == this.klass;

            if (matchAntecedent) {
                this.supportAntecedent++;

                if (matchConsequent) {
                    this.supportRule++;
                }
            }
        }
    }

    /**
     * Increment the counter of misses
     */
    public void incrementMisses() {
        this.misses += 1;
    }

    /**
     * Get the array of covered instances
     * 
     * @return the array of covered instances
     */
    public ArrayList<Integer> getCoveredInstances() {
        return this.coveredInstances;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#clone()
     */
    @Override
    public Object clone() {
        Rule cloned = new Rule(this.klass);

        Short[] newAntecedent = new Short[this.antecedent.size()];
        newAntecedent = this.antecedent.toArray(newAntecedent);
        cloned.add(newAntecedent);

        cloned.supportAntecedent = this.supportAntecedent;
        cloned.supportRule = this.supportRule;
        cloned.misses = this.misses;
        cloned.hits = this.hits;
        cloned.coveredInstances = new ArrayList<Integer>(this.coveredInstances);

        return cloned;
    }

    /**
     * Get the number of misses instances
     * 
     * @return the number of misses
     */
    public double getMisses() {
        return this.misses;
    }

    /**
     * Method used to calculate errors in PER
     * 
     * @param N number of hits
     * @param e number of errors
     * @return upper limit with confidence of 0.25
     */
    private static double errors(double N, double e) {
        double CF = 0.25;
        double VAL[] = { 0, 0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.00005, 0.0001, 0.0005, 0.001,
                0.005, 0.01, 0.05, 0.10, 0.20, 0.40, 1.00 };
        double DEV[] = { 100, 6.0, 5.61, 5.2, 4.75, 4.26, 3.89, 3.72, 3.29, 3.09, 2.58, 2.33, 1.65, 1.28, 0.84, 0.25,
                0.00 };

        double Val0, Pr, Coeff = 0;
        int i = 0;

        while (CF > VAL[i]) {
            i++;
        }

        Coeff = DEV[i - 1] + (DEV[i] - DEV[i - 1]) * (CF - VAL[i - 1]) / (VAL[i] - VAL[i - 1]);
        Coeff = Coeff * Coeff;

        if (e == 0) {
            return N * (1 - Math.exp(Math.log(CF) / N));
        } else {
            if (e < 0.9999) {
                Val0 = N * (1 - Math.exp(Math.log(CF) / N));

                return Val0 + e * (errors(N, 1.0) - Val0);
            } else {
                if (e + 0.5 >= N) {
                    return 0.67 * (N - e);
                } else {
                    Pr = (e + 0.5 + Coeff / 2 + Math.sqrt(Coeff * ((e + 0.5) * (1 - (e + 0.5) / N) + Coeff / 4)))
                            / (N + Coeff);

                    return (N * Pr - e);
                }
            }
        }
    }

    /**
     * Calculates the merit for current rule
     * 
     * @return the merit for current rule
     */
    public double getMerit() {
        double n = this.hits + this.misses;

        if (n <= 0)
            return 0.0;

        return (n - this.misses) / n;
    }

    /**
     * Increment the counter for the hits
     */
    public void incrementHits() {
        this.hits++;
    }
}
