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

/**
 * Configuration used to generate the classifier. It requires 5 parameters
 */
public class Config extends lac.algorithms.Config {
    /**
     * Number of rules combining for every example in rule generation phase
     */
    private double delta = 0.05;

    /**
     * When using FOIL, gain is used to select the best literals at that moment.
     * Itmeasures the information gained from adding this literal to the rule.
     */
    private double minBestGain = 0.7;

    /**
     * When an example is covered by a rule, it is not directly removed butits
     * weight is decreased applying a decay factor (α).
     */
    private double alpha = 2 / 3.0;

    /**
     * Number of rules to be used to predict an unseen example
     */
    private int k = 5;

    /**
     * Set the minimum best gain which will be allowed in rules
     * 
     * @param minBestGain the minimum best gain
     */
    public void setMinBestGain(Double minBestGain) {
        this.minBestGain = minBestGain;
    }

    /**
     * Set the decay factor (alpha)
     * 
     * @param alpha decay factor
     */
    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    /**
     * Set the number of rules used to predict an unseen example
     * 
     * @param k number of rules used to predict unseen examples
     */
    public void setK(Integer k) {
        this.k = k;
    }

    /**
     * Number of rules combining for every example in rule generation phase
     * 
     * @param delta number of rules used to combine to generate new rules
     */
    public void setDelta(Double delta) {
        this.delta = delta;
    }

    /**
     * Get the number of rules used to predict unseen examples
     * 
     * @return the number of rules to use while predicting unseen examples
     */
    public int getK() {
        return this.k;
    }

    /**
     * Get the alpha value
     * 
     * @return the alpha value
     */
    public double getAlpha() {
        return this.alpha;
    }

    /**
     * Get the minimum best gain for the rules
     * 
     * @return the minimum best gain
     */
    public double getMinBestGain() {
        return this.minBestGain;
    }

    /**
     * Get the delta value
     * 
     * @return the delta value
     */
    public double getDelta() {
        return delta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "k=" + this.k + " minBestGain=" + this.minBestGain + " delta=" + this.delta + " alpha=" + this.alpha;
    }
}
