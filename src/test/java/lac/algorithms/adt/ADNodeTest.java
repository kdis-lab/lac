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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;

public class ADNodeTest extends TestSuite {
    private Rule rule1 = new Rule(new Short[] { 0 }, Short.valueOf((short) 3));
    private Rule rule2 = new Rule(new Short[] { 0, 1 }, Short.valueOf((short) 3));
    private ADNode node1 = new ADNode(rule1);
    private ADNode node2 = new ADNode(rule2);

    @Before
    public void setup() {
        node2.childs.add(node1);
    }

    @Test
    public void isChildrenReturnsTrueWithChildren() {
        assertTrue(node1.isChild(rule2) == null);
    }

    @Test
    public void isChildrenReturnsFalseWithNoChildren() {
        assertFalse(node2.isChild(rule1) == null);
    }

    @Test
    public void hashCodeIsDelegatedToRule() {
        assertEquals(rule1.hashCode(), node1.hashCode());
        assertEquals(rule2.hashCode(), node2.hashCode());
    }

    @Test
    public void equalsIsOverwritten() {
        assertEquals(node1, new ADNode(rule1));
        assertEquals(node2, new ADNode(rule2));
    }

    @Test
    public void cloneReturnsNewInstance() {
        assertEquals(node1, node1.clone());
        assertNotSame(node1, node1.clone());
    }
}
