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
package lac.algorithms.cba;

import lac.algorithms.Rule;

/**
 * Class used to represent a selected rule while performing the post-processing
 * in CBA. A selectedRule is composed of the rule, the default klass and the
 * total of errors produced by them.
 */
@SuppressWarnings("rawtypes")
public class SelectedRule implements Comparable {
    /**
     * Default class for the selected rule
     */
    private short defaultKlass;

    /**
     * Total number of errors while using this rule
     */
    private Long totalErrors;

    /**
     * Selected rule
     */
    private Rule rule;

    /**
     * Constructor
     * 
     * @param rule         selected rule
     * @param defaultKlass default class
     * @param totalErrors  total number of errors while using this rule
     */
    public SelectedRule(Rule rule, short defaultKlass, Long totalErrors) {
        this.rule = rule;
        this.defaultKlass = defaultKlass;
        this.totalErrors = totalErrors;
    }

    /**
     * Get the rule
     * 
     * @return the selected rule
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Get the default class
     * 
     * @return the default class for the selected rule
     */
    public short getDefaultKlass() {
        return this.defaultKlass;
    }

    /**
     * Returns the total number of errors
     * 
     * @return total number of errors
     */
    public Long getTotalErrors() {
        return this.totalErrors;
    }

    /**
     * This has to be implemented to be able to sort array of selected rules in a
     * descending order of total errors
     */
    @Override
    public int compareTo(Object other) {
        SelectedRule s = (SelectedRule) other;

        return Long.compare(s.totalErrors, this.totalErrors);
    }
}
