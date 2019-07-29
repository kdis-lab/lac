/*
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
package lac.algorithms.l3;

/**
 * Configuration for the L3 algorithm. This algorithm requires two different
 * parameters.
 */
public class Config extends lac.algorithms.Config {
    /**
     * Minimum frequency of occurrence for the rules
     */
    private double minSup = 0.01;

    /**
     * Minimum confidence for the rules
     */
    private double minConf = 0.0;

    /**
     * Set the value of minimum support
     * 
     * @param minSup minimum value of frequency of occurrence
     */
    public void setMinSup(Double minSup) {
        this.minSup = minSup;
    }

    /**
     * Set the value of minimum support
     * 
     * @param minSup minimum value of frequency of occurrence
     */
    public void setMinSup(Integer minSup) {
        this.minSup = minSup;
    }

    /**
     * Set the value of minimum confidence
     * 
     * @param minConf minimum confidence for rules
     */
    public void setMinConf(Double minConf) {
        this.minConf = minConf;
    }

    /**
     * Set the value of minimum confidence
     * 
     * @param minConf minimum confidence for rules
     */
    public void setMinConf(Integer minConf) {
        this.minConf = minConf;
    }

    /**
     * Minimum value of support for the rules
     * 
     * @return minimum support
     */
    public double getMinSup() {
        return this.minSup;
    }

    /**
     * Minimum value of confidence for the rules
     * 
     * @return minimum value for confidence
     */
    public double getMinConf() {
        return this.minConf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "minSup=" + this.minSup + " minConf=" + this.minConf;
    }
}