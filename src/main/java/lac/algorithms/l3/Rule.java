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
package lac.algorithms.l3;

/**
 * Class used to represent class association rules in the L3 algorithm
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Constructor
     * 
     * @param antecedent items contained in this part of the rule
     * @param klass      consequent
     */
    public Rule(short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent items contained in this part of the rule
     * @param klass      consequent
     */
    public Rule(Short[] antecedent, short klass) {
        super(antecedent, klass);
    }

    /**
     * Sets the support for the antecedent of the rule
     * 
     * @param supportAntecedent frequency of occurrence for the antecedent
     */
    public void setSupportAntecedent(long supportAntecedent) {
        this.supportAntecedent = supportAntecedent;
    }

    /**
     * Support for the whole rule
     * 
     * @param supportRule frequency of occurrence for the whole rule
     */
    public void setSupportRule(long supportRule) {
        this.supportRule = supportRule;
    }

    /**
     * Set support for the consequent of the rule
     * 
     * @param supportKlass frequency of occurrence for the consequent of the rule
     */
    public void setSupportKlass(long supportKlass) {
        this.supportKlass = supportKlass;
    }
}