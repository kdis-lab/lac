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
 *
 * This algorithm was adapted from the SPMF Library, which is is licensed 
 * under the GNU GPL v3 license.
 * Fournier-Viger, P., Lin, C.W., Gomariz, A., Gueniche, T., Soltani, A., 
 * Deng, Z., Lam, H. T. (2016). The SPMF Open-Source Data Mining Library 
 * Version 2. Proc. 19th European Conference on Principles of Data Mining 
 * and Knowledge Discovery (PKDD 2016) Part III, Springer LNCS 9853, pp. 36-40.
 */
package lac.algorithms.cmar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class used to represent each node in the FP-Tree
 */
public class FPNode {
    /**
     * Item saved in current node
     */
    Short item = -1;

    /**
     * Frequency of occurrence for current item
     */
    long support = 1;

    /**
     * Support for this item for each class
     */
    HashMap<Short, Long> supportByklass;

    /**
     * Parent of the current node, null if it is root element
     */
    FPNode parent = null;

    /**
     * Array of immediate childs from current node
     */
    List<FPNode> childs = new ArrayList<FPNode>();

    /**
     * Next node with the same item, used to create the header table
     */
    FPNode nextNode = null;

    /**
     * Default constructor
     */
    FPNode() {
    }

    /**
     * Search in children the specified item, return the child with this item
     * 
     * @param item to look for
     * @return the node with the item, or null otherwise
     */
    FPNode getChildByItem(short item) {
        // Search item in childs
        for (FPNode child : childs) {
            if (child.item == item) {
                return child;
            }
        }

        return null;
    }
}
