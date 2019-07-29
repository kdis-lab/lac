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
package lac.algorithms.cpar;

import lac.data.Dataset;
import lac.utils.Utils;

/**
 * Class used to represent a Rule in the CPAR algorithm. It includes
 * functionality to calculate laplace accuracy
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * It is used to store the laplace accuracy
     */
    private double laplace;

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      consequent of the rule
     */
    public Rule(short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Determines Laplace expected error estimates
     * 
     * @param antecedent the antecedent of the given rule.
     * @param consequent the consequent of the given rule.
     * @return the Laplace accuracy.
     */
    protected void calculateLaplaceAccuracy(Dataset data) {
        int totalCounter = 0;
        int klassCounter = 0;

        for (int i = 0; i < data.size(); i++) {
            Short[] example = data.getInstance(i).asNominal();
            if (Utils.isSubset(antecedent, example)) {
                if (klass == data.getKlassInstance(i))
                    klassCounter++;
                totalCounter++;
            }
        }

        laplace = (double) (klassCounter + 1) / (double) (totalCounter + data.getNumberKlasses());
    }

    /**
     * Get the laplace accuracy for the training dataset
     * 
     * @return the laplace accuracy
     */
    public double getLaplace() {
        return laplace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#toString()
     */
    public String toString() {
        return this.antecedent.toString() + " -> " + this.getKlass() + " Laplace: " + this.getLaplace();
    }
}
