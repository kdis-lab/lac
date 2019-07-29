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
package lac.data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class used to represent each instance contained into a dataset
 */
public class Instance {
    /**
     * Array with all the values contained in this instance
     */
    private ArrayList<Object> instance;

    /**
     * Constructor
     * 
     * @param length number of values contained in this instance
     */
    public Instance(int length) {
        this.instance = new ArrayList<Object>(Collections.nCopies(length, null));
    }

    /**
     * Get the instance as if all the values were nominal
     * 
     * @return an array the values (codified using the internal representation)
     */
    public Short[] asNominal() {
        Short[] example = new Short[this.instance.size()];
        example = this.instance.toArray(example);
        return example;
    }

    /**
     * Get the class for this instance
     * 
     * @return the internal representation for this instance
     */
    public Short getKlass() {
        return (Short) instance.get(this.instance.size() - 1);
    }

    /**
     * Set the value of the class in this instance
     * 
     * @param klass value for the class in this instance
     */
    public void setKlass(Short klass) {
        instance.set(this.instance.size() - 1, klass);
    }

    /**
     * Set the attribute value situated in the position specified
     * 
     * @param j     position of the attribute in current instance
     * @param value for this attribute in this instance
     */
    public void set(int j, Object value) {
        instance.set(j, value);
    }
}
