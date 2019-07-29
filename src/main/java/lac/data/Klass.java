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

/**
 * Class used to represent the metadata information for the class in the dataset
 */
public class Klass {
    /**
     * All the possible values that the class could take
     */
    private String[] values;

    /**
     * Constructor
     * 
     * @param values all the possible values that class could take
     */
    public Klass(String[] values) {
        this.values = values;
    }

    /**
     * Get the value situated at specified position
     * 
     * @param i position to get the value
     * @return the value
     */
    public String getValue(int i) {
        return this.values[i];
    }

    /**
     * Get the number of values for the class
     * 
     * @return the number of values for the class
     */
    public int getNumberValues() {
        return this.values.length;
    }

    /**
     * Get all possible values for this class
     * 
     * @return all the possible classes
     */
    public String[] getValues() {
        return this.values;
    }
}
