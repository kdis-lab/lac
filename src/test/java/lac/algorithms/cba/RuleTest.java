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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import lac.data.ArffDataset;
import lac.data.Dataset;

public class RuleTest {
    private Dataset dataset;

    @Before
    public void setup() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/dataset-nominal.arff").getFile());
        dataset = new ArffDataset(file.getAbsolutePath());
    }

    @Test
    public void isSubsetReturnsTrueWithSubset() {
        Rule rule1 = new Rule((short) 3);
        rule1.add((short) 1);
        Rule rule2 = new Rule((short) 3);
        rule2.add(new short[] { 0, 1 });

        assertTrue(rule1.isSubset(rule2));
    }

    @Test
    public void isSubsetReturnsFalseWithSubsetButDifferentKlass() {
        Rule rule1 = new Rule((short) 4);
        rule1.add((short) 1);
        Rule rule2 = new Rule((short) 3);
        rule2.add(new short[] { 0, 1 });

        assertFalse(rule1.isSubset(rule2));
    }

    @Test
    public void isSubsetReturnsFalseWithNoSubset() {
        Rule rule1 = new Rule((short) 3);
        rule1.add((short) 1);
        Rule rule2 = new Rule((short) 3);
        rule2.add(new short[] { 0, 1 });

        assertFalse(rule2.isSubset(rule1));
    }

    @Test
    public void calculateSupportsCalculatesAllMetrics() {
        Rule rule1 = new Rule((short) 4);
        rule1.add((short) 0);

        rule1.calculateSupports(dataset);

        assertEquals(2, rule1.getSupportAntecedent());
        assertEquals(2, rule1.getSupportRule());
        assertEquals(2, rule1.getSupportKlass());
        assertEquals(1, rule1.getMisses());
        assertEquals(0.685, rule1.getPessimisticErrorRate(), 0.01);
    }

    @Test
    public void replace() {
        Rule rule1 = new Rule((short) 4);
        rule1.add((short) 0);

        rule1.addReplace(new Replace(0, 1, (short) 2));
        assertEquals(1, rule1.getNumberReplace());

        rule1.addReplace(new Replace(0, 1, (short) 2));
        assertEquals(2, rule1.getNumberReplace());
    }

    @Test
    public void markByDefaultIsFalse() {
        Rule rule = new Rule();
        assertFalse(rule.isMark());
    }

    @Test
    public void markIsSetted() {
        Rule rule = new Rule();
        rule.mark();
        assertTrue(rule.isMark());
    }

    @Test
    public void isPrecedentByConfidence() {
        Rule rule1 = new Rule((short) 5);
        rule1.add((short) 0);
        rule1.calculateSupports(dataset);

        Rule rule2 = new Rule((short) 4);
        rule2.add((short) 1);
        rule2.calculateSupports(dataset);

        assertTrue(rule1.isPrecedence(rule2));
    }

    @Test
    public void isPrecedentBySupportRule() {
        Rule rule1 = new Rule((short) 5);
        rule1.add((short) 1);
        rule1.calculateSupports(dataset);

        Rule rule2 = new Rule((short) 4);
        rule2.add((short) 0);
        rule2.calculateSupports(dataset);

        assertFalse(rule1.isPrecedence(rule2));
    }

    @Test
    public void isPrecedentBySizeAntecedent() {
        Rule rule1 = new Rule((short) 5);
        rule1.add((short) 1);
        rule1.calculateSupports(dataset);

        Rule rule2 = new Rule((short) 4);
        rule2.add(new short[] { 0, 2 });
        rule2.calculateSupports(dataset);

        assertTrue(rule1.isPrecedence(rule2));
        assertFalse(rule2.isPrecedence(rule1));
    }

    @Test
    public void klassCovered() {
        Rule rule = new Rule();
        rule.incrementKlassCovered((short) 2);
        assertEquals(new Long(1), rule.getKlassesCovered((short) 2));
        rule.incrementKlassCovered((short) 3);
        assertEquals(new Long(1), rule.getKlassesCovered((short) 2));
        assertEquals(new Long(1), rule.getKlassesCovered((short) 3));
        rule.incrementKlassCovered((short) 2);
        assertEquals(new Long(2), rule.getKlassesCovered((short) 2));
        assertEquals(new Long(1), rule.getKlassesCovered((short) 3));
    }

    @Test
    public void isCombinableWithDifferentKlass() {
        Rule rule1 = new Rule((short) 1);
        Rule rule2 = new Rule((short) 2);

        assertFalse(rule1.isCombinable(rule2));
    }

    @Test
    public void isCombinableWithDifferentSize() {
        Rule rule1 = new Rule((short) 1);
        rule1.add((short) 0);
        Rule rule2 = new Rule((short) 1);
        rule2.add(new short[] { 0, 1 });

        assertFalse(rule1.isCombinable(rule2));
    }

    @Test
    public void isCombinableWithOnlyOneDifferentItemByLexicographyOrder() {
        Rule rule1 = new Rule((short) 1);
        rule1.add(new short[] { 0, 2 });
        Rule rule2 = new Rule((short) 1);
        rule2.add(new short[] { 0, 1 });

        assertFalse(rule1.isCombinable(rule2));
        assertTrue(rule2.isCombinable(rule1));
    }

    @Test
    public void compareToByConfidence() {
        Rule rule1 = new Rule((short) 5);
        rule1.add(new short[] { 2 });
        rule1.calculateSupports(dataset);
        Rule rule2 = new Rule((short) 4);
        rule2.add(new short[] { 0 });
        rule2.calculateSupports(dataset);

        assertEquals(1, rule1.compareTo(rule2));
        assertEquals(-1, rule2.compareTo(rule1));
    }

    @Test
    public void compareToBySupportRule() {
        Rule rule1 = new Rule((short) 5);
        rule1.add(new short[] { 1 });
        rule1.calculateSupports(dataset);
        Rule rule2 = new Rule((short) 4);
        rule2.add(new short[] { 0 });
        rule2.calculateSupports(dataset);

        assertEquals(1, rule1.compareTo(rule2));
        assertEquals(-1, rule2.compareTo(rule1));
    }

    @Test
    public void compareToByTime() throws InterruptedException {
        Rule rule1 = new Rule((short) 4);
        rule1.add(new short[] { 0 });
        rule1.calculateSupports(dataset);
        Thread.sleep(100); // To guarantee different time between rule1 and rule2
        Rule rule2 = new Rule((short) 4);
        rule2.add(new short[] { 0 });
        rule2.calculateSupports(dataset);

        assertEquals(-1, rule1.compareTo(rule2));
        assertEquals(1, rule2.compareTo(rule1));
        assertEquals(0, rule1.compareTo(rule1));
    }
    
    @Test
    public void cloneReturnsNewInstance() {
        Rule rule1 = new Rule((short) 4);
        rule1.addReplace(new Replace(1, 1, (short) 1));
        rule1.add(new short[] { 0 });
        rule1.calculateSupports(dataset);
        
        assertNotSame(rule1, rule1.clone());
        assertEquals(rule1, rule1.clone());
        assertEquals(rule1.getPessimisticErrorRate(), ((Rule) rule1.clone()).getPessimisticErrorRate(), 0.01);
        assertEquals(rule1.getSupportRule(), ((Rule) rule1.clone()).getSupportRule());
        assertEquals(rule1.getSupportKlass(), ((Rule) rule1.clone()).getSupportKlass());
        assertEquals(rule1.getSupportAntecedent(), ((Rule) rule1.clone()).getSupportAntecedent());
        assertEquals(rule1.getMisses(), ((Rule) rule1.clone()).getMisses());
        assertEquals(rule1.getNumberReplace(), ((Rule) rule1.clone()).getNumberReplace());
    }

}
