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
package lac.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestSuite;
import lac.data.Instance;

public class RuleTest extends TestSuite {

    private Instance example = new Instance(2);

    @Before
    public void setup() {
        example.set(0, new Short((short) 1));
        example.set(1, new Short((short) 2));
    }

    @Test
    public void emptyConstructorSetAllAsEmpty() {
        Rule rule = new Rule();
        assertEquals(0, rule.getSupportRule());
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportKlass());
        assertTrue(rule.getAntecedent().isEmpty());
    }

    @Test
    public void constructorWithKlassSetKlass() {
        short klass = 2;
        Rule rule = new Rule(klass);

        assertEquals(0, rule.getSupportRule());
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportKlass());
        assertTrue(rule.getAntecedent().isEmpty());
        assertEquals(klass, rule.getKlass());
    }

    @Test
    public void constructorWithAntecedentKlassAsNativeTypesSetAntecedentKlass() {
        short klass = 2;
        short[] antecedent = new short[] { 0, 1 };
        Short[] antecedentAsObject = new Short[] { 0, 1 };
        Rule rule = new Rule(antecedent, klass);

        assertEquals(0, rule.getSupportRule());
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportKlass());
        assertEquals(new ArrayList<Short>(Arrays.asList(antecedentAsObject)), rule.getAntecedent());
        assertEquals(klass, rule.getKlass());
    }

    @Test
    public void constructorWithAntecedentKlassAsShortSetAntecedentKlass() {
        short klass = 2;
        Short[] antecedent = new Short[] { 0, 1 };
        Rule rule = new Rule(antecedent, klass);

        assertEquals(0, rule.getSupportRule());
        assertEquals(0, rule.getSupportAntecedent());
        assertEquals(0, rule.getSupportKlass());
        assertEquals(new ArrayList<Short>(Arrays.asList(antecedent)), rule.getAntecedent());
        assertEquals(klass, rule.getKlass());
    }

    @Test
    public void getConfidenceWithSupportAntecedentAs0Returns0() {
        Rule rule = new Rule();
        assertEquals(0.0, rule.getConfidence(), 0.0);
    }

    @Test
    public void getConfidenceWithSupportAntecedentGreater() {
        Rule rule = new Rule();
        rule.supportAntecedent = 10;
        rule.supportRule = 5;
        assertEquals(0.5, rule.getConfidence(), 0.0);
    }

    @Test
    public void getSupportRuleReturnSupportRule() {
        Rule rule = new Rule();
        rule.supportRule = 5;
        assertEquals(5, rule.getSupportRule(), 0.0);
    }

    @Test
    public void getSupportConsequentReturnSupportKlass() {
        Rule rule = new Rule();
        rule.supportKlass = 5;
        assertEquals(5, rule.getSupportKlass(), 0.0);
    }

    @Test
    public void matchingWithoutAntecedentReturnsTrue() {
        Rule rule = new Rule();
        assertTrue(rule.matching(example.asNominal()));
    }

    @Test
    public void matchingWithAntecedentInCommonReturnTrue() {
        Rule rule = new Rule();
        rule.add(new short[] { 1, 2 });
        assertTrue(rule.matching(example.asNominal()));
    }

    @Test
    public void matchingWithoutAntecedentInCommonReturnFalse() {
        Rule rule = new Rule();
        rule.add(new short[] { 1, 3 });
        assertFalse(rule.matching(example.asNominal()));
    }

    @Test
    public void sizeReturnsSizeAntecedent() {
        Rule rule = new Rule();
        rule.add(new short[] { 1, 3 });
        assertEquals(2, rule.size());
    }

    @Test
    public void getItemsByIndexOfAntecedent() {
        Rule rule = new Rule();
        Short[] antecedent = new Short[] { 1, 3 };

        rule.add(antecedent);
        for (int i = 0; i < antecedent.length; i++) {
            assertEquals(antecedent[i], rule.getAntecedent().get(i));
        }
    }

    @Test
    public void klassCanBeChanged() {
        short klass = 5;
        Rule rule = new Rule();
        rule.setKlass(klass);
        assertEquals(klass, rule.getKlass());
    }

    @Test
    public void toStringReturnsStringContainingAllInformation() {
        short klass = 5;
        Rule rule = new Rule();
        rule.setKlass(klass);
        rule.add(new short[] { 1, 3 });
        rule.supportRule = 10;
        rule.supportAntecedent = 20;
        assertEquals(rule.getAntecedent().toString() + " -> " + rule.getKlass() + " Sup: " + rule.getSupportRule()
                + " Conf: " + rule.getConfidence(), rule.toString());
    }
}
