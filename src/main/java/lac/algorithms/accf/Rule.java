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
package lac.algorithms.accf;

/**
 * Class for representing each mined rule in ACCF
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      used as consequent of the rule
     */
    public Rule(short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      used as consequent of the rule
     */
    public Rule(Short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Set the support for the antecedent rule
     * 
     * @param supportAntecedent support for the antecedent of the rule
     */
    public void setSupportAntecedent(long supportAntecedent) {
        this.supportAntecedent = supportAntecedent;
    }

    /**
     * Set the support for the current rule
     * 
     * @param supportRule support for the rule
     */
    public void setSupportRule(long supportRule) {
        this.supportRule = supportRule;
    }

    /**
     * Set the support for the consequent of the current rule
     * 
     * @param supportKlass support for the consequent
     */
    public void setSupportKlass(long supportKlass) {
        this.supportKlass = supportKlass;
    }
}
