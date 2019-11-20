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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestSuite;

public class FPTreeTest extends TestSuite {

    @SuppressWarnings("serial")
    @Test
    public void addInstanceUpdatesSupportAndHeader() {
        FPTree tree = new FPTree();
        HashMap<Short, Long> mapSupport = new HashMap<Short, Long>();
        mapSupport.put((short) 0, 1L);
        mapSupport.put((short) 1, 2L);
        mapSupport.put((short) 2, 3L);

        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 2), new Short((short) 6));
        tree.createHeaderList(mapSupport);
        tree.addInstance(Arrays.asList((short) 4), new Short((short) 5));

        assertEquals(new HashSet<Short>(Arrays.asList((short) 0, (short) 1, (short) 2, (short) 4)),
                tree.mapItemNodes.keySet());
        assertEquals(1L, tree.mapItemNodes.get((short) 0).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 1).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 2).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 4).support);
        Map<Short, Long> supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 0).supportByklass);
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 1).supportByklass);
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 2).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 4).supportByklass);
    }

    @SuppressWarnings("serial")
    @Test
    public void addInstanceWithChildUpdatesSupportAndHeader() {
        FPTree tree = new FPTree();
        HashMap<Short, Long> mapSupport = new HashMap<Short, Long>();
        mapSupport.put((short) 0, 1L);
        mapSupport.put((short) 1, 2L);
        mapSupport.put((short) 2, 3L);

        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 2), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 5));

        assertEquals(new HashSet<Short>(Arrays.asList((short) 0, (short) 1, (short) 2, (short) 3)),
                tree.mapItemNodes.keySet());
        assertEquals(3L, tree.mapItemNodes.get((short) 0).support);
        assertEquals(3L, tree.mapItemNodes.get((short) 1).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 2).support);
        assertEquals(2L, tree.mapItemNodes.get((short) 3).support);
        Map<Short, Long> supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 0).supportByklass);
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 1).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 2).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 3).supportByklass);
    }

    @SuppressWarnings("serial")
    @Test
    public void addPrefixPathAddPath() {
        FPTree tree = new FPTree();
        HashMap<Short, Long> mapSupport = new HashMap<Short, Long>();
        mapSupport.put((short) 0, 1L);
        mapSupport.put((short) 1, 2L);
        mapSupport.put((short) 2, 3L);

        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 2), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 5));

        FPNode node1 = new FPNode();
        node1.support = 3;
        node1.item = 0;
        node1.supportByklass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };

        FPNode node2 = new FPNode();
        node2.support = 3;
        node2.item = 1;
        node2.supportByklass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };

        List<FPNode> prefixPath = new ArrayList<FPNode>(Arrays.asList(node2, node1));
        Map<Short, Long> mapSupportBeta = new HashMap<Short, Long>();
        mapSupportBeta.put((short) 0, 3L);
        mapSupportBeta.put((short) 1, 3L);

        tree.addPrefixPath(prefixPath, mapSupportBeta, 1);

        assertEquals(new HashSet<Short>(Arrays.asList((short) 0, (short) 1, (short) 2, (short) 3)),
                tree.mapItemNodes.keySet());
        assertEquals(6L, tree.mapItemNodes.get((short) 0).support);
        assertEquals(3L, tree.mapItemNodes.get((short) 1).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 2).support);
        assertEquals(2L, tree.mapItemNodes.get((short) 3).support);
        Map<Short, Long> supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 4L);
                put((short) 5, 2L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 0).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 1).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 2).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 3).supportByklass);
    }

    @SuppressWarnings("serial")
    @Test
    public void addPrefixPathAddNewPath() {
        FPTree tree = new FPTree();
        HashMap<Short, Long> mapSupport = new HashMap<Short, Long>();
        mapSupport.put((short) 0, 1L);
        mapSupport.put((short) 1, 2L);
        mapSupport.put((short) 2, 3L);

        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 2), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 6));
        tree.addInstance(Arrays.asList((short) 0, (short) 1, (short) 3), new Short((short) 5));

        FPNode node1 = new FPNode();
        node1.support = 3;
        node1.item = 2;
        node1.supportByklass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };

        FPNode node2 = new FPNode();
        node2.support = 3;
        node2.item = 4;
        node2.supportByklass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };

        List<FPNode> prefixPath = new ArrayList<FPNode>(Arrays.asList(node2, node1));
        Map<Short, Long> mapSupportBeta = new HashMap<Short, Long>();
        mapSupportBeta.put((short) 4, 3L);
        mapSupportBeta.put((short) 2, 3L);

        tree.addPrefixPath(prefixPath, mapSupportBeta, 1);

        assertEquals(new HashSet<Short>(Arrays.asList((short) 0, (short) 1, (short) 2, (short) 3)),
                tree.mapItemNodes.keySet());
        assertEquals(3L, tree.mapItemNodes.get((short) 0).support);
        assertEquals(3L, tree.mapItemNodes.get((short) 1).support);
        assertEquals(1L, tree.mapItemNodes.get((short) 2).support);
        assertEquals(2L, tree.mapItemNodes.get((short) 3).support);
        Map<Short, Long> supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 0).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 2L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 1).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 2).supportByklass);
        supportByKlass = new HashMap<Short, Long>() {
            {
                put((short) 6, 1L);
                put((short) 5, 1L);
            }
        };
        assertEquals(supportByKlass, tree.mapItemNodes.get((short) 3).supportByklass);
    }}
