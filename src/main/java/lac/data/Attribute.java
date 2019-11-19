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
 * Attribute contained into the dataset
 */
public class Attribute {
    /**
     * Numeric constant to define numeric attributes
     */
    static int TYPE_NUMERIC = 0;

    /**
     * Nominal constant to define numeric attributes
     */
    static int TYPE_NOMINAL = 1;

    /**
     * Name of the attribute
     */
    private String name = null;

    /**
     * Field used to determine which type of attribute is
     */
    private int type;

    /**
     * Values which could take this attribute
     */
    private String[] values;

    /**
     * Constructor
     * 
     * @param name of the attribute
     * @param type of the attribute
     */
    public Attribute(String name, int type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Constructor
     * 
     * @param name   of the attribute
     * @param values possible values which could take this attribute
     */
    public Attribute(String name, String[] values) {
        this(name, TYPE_NOMINAL);
        this.values = values;
    }

    /**
     * Checks if attribute is nominal or not
     * 
     * @return true if it is nominal, false otherwise
     */
    public Boolean isNominal() {
        return this.type == TYPE_NOMINAL;

    }

    /**
     * Checks if attribute is numeric or not
     * 
     * @return true if it is numeric, false otherwise
     */
    public Boolean isNumeric() {
        return this.type == TYPE_NUMERIC;

    }

    /**
     * Get the number of nominal values
     * 
     * @return the number of values
     * @throws Exception
     */
    public int getNumberValues() throws Exception {
        if (!this.isNominal())
            throw new Exception();

        return this.values.length;
    }

    /**
     * Get the values for this attribute
     * 
     * @return the values for the attribute
     */
    public String[] getValues() {
        return this.values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (this.values != null && this.values.length >= 0) {
            return "name=" + this.name + " values=" + String.join(",", this.values);
        } else {
            return "name=" + this.name;
        }
    }

    /**
     * Get the name of the attribute
     * 
     * @return the name of the attribute
     */
    public String getName() {
        return this.name;
    }
}
