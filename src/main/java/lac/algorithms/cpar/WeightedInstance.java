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

import java.io.Serializable;

/**
 * Class used for representing an instance with its respective weights
 */
class WeightedInstance implements Cloneable, Serializable {
    private static final long serialVersionUID = -1404375617431520447L;

    /**
     * Instance being represented
     */
    short[] instance;

    /**
     * Default weight
     */
    double weight = 1.0;

    /**
     * Constructor
     * 
     * @param example being represented
     */
    WeightedInstance(Short[] example) {
        instance = new short[example.length];
        for (int i = 0; i < example.length; i++)
            instance[i] = example[i];
    }

    /**
     * Constructor
     * 
     * @param example being represented
     * @param weight  of the instance
     */
    WeightedInstance(short[] example, double weight) {
        instance = new short[example.length];
        System.arraycopy(example, 0, instance, 0, example.length);
        this.weight = weight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new WeightedInstance(instance, weight);
    }
}