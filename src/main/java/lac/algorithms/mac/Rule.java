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
package lac.algorithms.mac;

import java.util.Set;

/**
 * Class for representing a rule in MAC. It adds support for tidsets
 */
public class Rule extends lac.algorithms.Rule {
    /**
     * Tidset of the antecedent
     */
    private Set<Integer> tidsetAntecedent;

    /**
     * Tidset of the rule
     */
    private Set<Integer> tidsetRule;

    /**
     * Constructor
     * 
     * @param klass to be used as consequent
     */
    public Rule(short klass) {
        super(klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      to be used as consequent
     */
    public Rule(short antecedent, short klass) {
        super(new short[] { antecedent }, klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      to be used as consequent
     */
    public Rule(Short[] antecedent, Short klass) {
        super(antecedent, klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent of the rule
     * @param klass      to be used as consequent
     */
    public Rule(short[] antecedent, Short klass) {
        super(antecedent, klass);
    }

    /**
     * Constructor
     * 
     * @param antecedent       of the rule
     * @param tidsetAntecedent set of ids where the antecedent is present
     * @param klass            to be used as consequent
     * @param tidsetRule       set of ids where the rule is present
     */
    public Rule(Short[] antecedent, Set<Integer> tidsetAntecedent, Short klass, Set<Integer> tidsetRule) {
        super(antecedent, klass);

        this.tidsetAntecedent = tidsetAntecedent;
        this.tidsetRule = tidsetRule;
    }

    /**
     * Constructor
     * 
     * @param antecedent       of the rule
     * @param tidsetAntecedent set of ids where the antecedent is present
     * @param klass            to be used as consequent
     * @param tidsetRule       set of ids where the rule is present
     */
    public Rule(short antecedent, Set<Integer> tidsetAntecedent, Short klass, Set<Integer> tidsetRule) {
        this(new Short[] { antecedent }, tidsetAntecedent, klass, tidsetRule);
    }

    /**
     * @param tidsetAntecedent
     */
    public void setTidsetAntecedent(Set<Integer> tidsetAntecedent) {
        this.tidsetAntecedent = tidsetAntecedent;
    }

    /**
     * Set the tidset for the whole rule
     * 
     * @param tidsetRule set of ids where the rule is present
     */
    public void setTidsetRule(Set<Integer> tidsetRule) {
        this.tidsetRule = tidsetRule;
    }

    /**
     * Check if two rules are combinable
     * 
     * @param other to be checked if they are combinable
     * @return true, if the two rules are combinable, and false otherwise
     */
    public boolean isCombinable(Rule other) {
        return this.klass == other.klass;
    }

    /**
     * Get the tidset for the whole rule
     * 
     * @return the tidset for the rule
     */
    public Set<Integer> getTidsetRule() {
        return tidsetRule;
    }

    /**
     * Get the tidset for the antecedent
     * 
     * @return the tidset for the antecedent
     */
    public Set<Integer> getTidsetAntecedent() {
        return tidsetAntecedent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#getSupportAntecedent()
     */
    @Override
    public long getSupportAntecedent() {
        return this.tidsetAntecedent.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see lac.algorithms.Rule#getSupportRule()
     */
    @Override
    public long getSupportRule() {
        return this.tidsetRule.size();
    }
}
