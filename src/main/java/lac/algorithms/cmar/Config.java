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
package lac.algorithms.cmar;

/**
 * Configuration used to generate the classifier. It requires 3 parameters
 */
public class Config extends lac.algorithms.Config {
    /**
     * Parameter used to represent the minimum value of support for the mined rules
     */
    private double minSup = 0.01;

    /**
     * Parameter used to represent the minimum value of confidence for the mined
     * rules
     */
    private double minConf = 0.5;

    /**
     * Parameter used to represent the minimum number of times that an instance need
     * to be covered
     */
    private int delta = 4;

    /**
     * Set the minimum support for the rules
     * 
     * @param minSup minimum value for support
     */
    public void setMinSup(Double minSup) {
        this.minSup = minSup;
    }

    /**
     * Set the minimum confidence for the rules
     * 
     * @param minConf minimum value for confidence
     */
    public void setMinConf(Double minConf) {
        this.minConf = minConf;
    }

    /**
     * Set the minimum times that an instance has to be covered
     * 
     * @param delta minimum number of times
     */
    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    /**
     * Get the minimum support
     * 
     * @return the minimum support
     */
    public double getMinSup() {
        return this.minSup;
    }

    /**
     * Get the minimum confidence
     * 
     * @return the minimum confidence
     */
    public double getMinConf() {
        return this.minConf;
    }

    /**
     * Get the delta value
     * 
     * @return delta value
     */
    public Integer getDelta() {
        return this.delta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "minSup=" + this.minSup + " minConf=" + this.minConf + " delta=" + this.delta;
    }
}