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
 * Class used to represent each literal, its gain and if it has been selected or
 * not
 */
class Literal implements Cloneable, Serializable {
    private static final long serialVersionUID = -6184100195125127407L;

    /**
     * Gain for the current literal
     */
    double gain;

    /**
     * Flag used to represent if current literal was added to current rule
     */
    boolean selected;

    /**
     * Constructor
     * 
     * @param gain
     * @param selected
     */
    public Literal(double gain, boolean selected) {
        this.gain = gain;
        this.selected = selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Literal(gain, selected);
    }
}