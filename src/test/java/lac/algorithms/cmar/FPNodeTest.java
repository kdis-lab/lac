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
package lac.algorithms.cmar;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class FPNodeTest extends TestSuite {
    FPNode node = new FPNode();
    FPNode node1 = new FPNode();
    FPNode node2 = new FPNode();

    @Before
    public void setup() {
        node1.item = 3;
        node.childs.add(node1);

        node2.item = 4;
        node.childs.add(node2);
        
    }
    
    @Test
    public void getChildByIdReturnsItem() {
        assertEquals(node2, node.getChildByItem(node2.item));
    }

    @Test
    public void getChildByIdReturnsNullWhenIsNotFound() {
        assertEquals(null, node.getChildByItem((short) 300000));
    }
}
