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

/**
 * Configuration used for ADT algorithms. It includes two different measures.
 * First, confidence is taken into account to obtain rules. Then, merit is used
 * to remove those rules which missclassify many instances
 */
public class Config extends lac.algorithms.Config {
    /**
     * Minimum value for the confidence measure
     */
    private double minConf = 0.5;

    /**
     * Minimum value for the merit measure
     */
    private double minMerit = 0.1;

    /**
     * Get the minimum confidence
     * 
     * @return the minimum confidence value
     */
    public double getMinConf() {
        return this.minConf;
    }

    /**
     * Sets the minimum confidence
     * 
     * @param confidence
     */
    public void setMinConf(Double confidence) {
        this.minConf = confidence;
    }

    /**
     * Get the minimum value for the merit measure
     * 
     * @return the minimum value for the merit measure
     */
    public double getMinMerit() {
        return this.minMerit;
    }

    /**
     * Sets the minimum value for the merit measure
     * 
     * @param minMerit minimum value for merit
     */
    public void setMinMerit(Double minMerit) {
        this.minMerit = minMerit;
    }
}
