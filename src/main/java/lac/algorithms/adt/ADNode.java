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
package lac.algorithms.adt;

import java.util.ArrayList;
import java.util.List;

import lac.utils.Utils;

/**
 * Class used to represent each node forming the ADT tree
 */
public class ADNode implements Cloneable {
    /**
     * Parent of the current node
     */
    ADNode parent = null;

    /**
     * Rule in the current node
     */
    Rule rule = null;

    /**
     * Array with all the childs from this node
     */
    List<ADNode> childs = new ArrayList<ADNode>();

    /**
     * Constructor
     * 
     * @param rule in current node
     */
    public ADNode(Rule rule) {
        this.rule = rule;
        this.parent = null;
    }

    /**
     * Check if there is some node as child containing the specified rule
     * 
     * @param rule to search in childs
     * @return true if rule is contained in the childs
     */
    public ADNode isChild(Rule rule) {
        ADNode child = null;
        for (int i = 0; i < childs.size() && child == null; i++) {
            if (Utils.isSubset(rule.getAntecedent(), childs.get(i).rule.getAntecedent()))
                child = childs.get(i);
        }
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof ADNode) {
            ADNode ptr = (ADNode) v;
            retVal = ptr.rule.equals(rule) && ptr.parent == parent;
        }

        return retVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.rule.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        ADNode node = new ADNode((Rule) rule.clone());
        node.parent = parent;
        node.childs = new ArrayList<ADNode>(childs);
        return node;
    }
}
